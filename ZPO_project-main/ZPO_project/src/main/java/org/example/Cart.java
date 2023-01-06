package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.tuple.ImmutableTriple;


public class Cart {

    private Connection connection;

    public Cart(Connection conn) {
        connection = conn;
    }


    //Map<String, Integer> cartProducts = new HashMap<String, Integer>();
    //Dictionary dict = new Hashtable();
    private static int uniqueSequence = 0;


    private int cartId;
    private Map<String, Integer> cartProducts = new HashMap<String, Integer>();


    public void setCartId() {
        this.cartId = uniqueSequence++;
    }

    public int getCartId() {
        return cartId;
    }

    public String addProduct(String productName, int number) {
        Connect connect = new Connect();
//            PreparedStatement selectAllSt = connection.prepareStatement("select availability from products where name like'" + productName + "';");
//            ResultSet rsAllSt = selectAllSt.executeQuery();

        Products products = new Products(connect.makeConnection(), productName);

        int availability = products.getAvailability();

        if (availability >= number) {
            cartProducts.put(productName, number);
            return "dodano";
        } else {
            return "brak";
        }

    }

    public String changeProductValueLess(String productName) {
        int number = cartProducts.get(productName);
        cartProducts.replace(productName, number - 1);
        return "done";
    }

    public String changeProductValueMore(String productName) {
        int number = cartProducts.get(productName);
        cartProducts.replace(productName, number + 1);
        return "done";
    }

    public String deleteOneProduct(String productName) {
        cartProducts.remove(productName);
        return "done";
    }

    public String clearCart() {
        cartProducts.clear();
        return "done";
    }

    public void showCart() {
        Set<Map.Entry<String, Integer>> s = cartProducts.entrySet();

        for (Map.Entry<String, Integer> it : s) {
            String productName = it.getKey();
            Integer number = it.getValue();
            System.out.println("Produkt: " + productName + ", liczba: " + number);
        }
    }

    public void deleteCart() {

    }

    public String reloadCart() {

        return "done";
    }

    public void checkOrdersHistory(int clientId) {


        try {
            Connect connect = new Connect();

            System.out.println("Imię i nazwisko klienta:");
            PreparedStatement selectAllSt2 = connection.prepareStatement("select first_name, last_name from users where id=" + clientId + ";");
            ResultSet client = selectAllSt2.executeQuery();
            //client.next();
            printResultSet(client);

            System.out.println("Szczegóły zamówienia:");
            System.out.println("id zamówienia, data utworzenia, wartość zamówienia [zł]");

            PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, order_value from order_details where client_id='" + clientId + "';");
            ResultSet orderDetails = selectAllSt.executeQuery();

            while (orderDetails.next()) {
                //orderDetails.next();
                int orderId = orderDetails.getInt(1);
                printResultSet(orderDetails);

                System.out.println("Zamówione produkty:");
                PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from order_items where order_id='" + orderId + "' group by order_id;");
                ResultSet orderedItems = selectAllSt1.executeQuery();
                orderedItems.next();
                String productsId = orderedItems.getString(1);
                int quantity = orderedItems.getInt(3);
                double value = orderedItems.getInt(4);
                System.out.println(value + " zł");
                System.out.println(quantity + " szt.");

                PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productsId + "';");
                ResultSet name = selectAllSt5.executeQuery();
                //name.next();
                printResultSet(name);


            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //potrzebujhe do funkcji dodac indeks uzytkownika!!!!!
    public ImmutableTriple<String, Double, String> saveCart(int clientId) {


        Set<Map.Entry<String, Integer>> s = cartProducts.entrySet();
        //cartId = uniqueSequence++;
        //int clientId = 3;


        try {
            Connect connect = new Connect();
            Statement stmt = connection.createStatement();

            String sql = "INSERT INTO carts(client_id) VALUES(" + clientId + ");";
            stmt.executeUpdate(sql);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        double cartValue = 0;

        for (Map.Entry<String, Integer> it : s) {
            String productName = it.getKey();
            Integer number = it.getValue();

            try {
                Connect connect = new Connect();
                Statement stmt = connection.createStatement();

//                PreparedStatement selectAllSt1 = connection.prepareStatement("select id from products where name like'" + productName + "';");
//                ResultSet productId = selectAllSt1.executeQuery();
//                productId.next();
//                //printResultSet(productId);
//                String idString = productId.getString(1);

                Products products = new Products(connect.makeConnection(), productName);
                String idString = products.getId();
                double priceInt = products.getPrice();

//                PreparedStatement selectAllSt2 = connection.prepareStatement("select price from products where name like'" + productName + "';");
//                ResultSet price = selectAllSt2.executeQuery();
//                price.next();
//                double priceInt = price.getDouble(1);

                double itemsValue = number * priceInt;

                PreparedStatement selectAllSt3 = connection.prepareStatement("select id from carts order by id desc  limit 1;");
                ResultSet cartTempId = selectAllSt3.executeQuery();
                cartTempId.next();
                String cartId = cartTempId.getString(1);

                String sql = "INSERT INTO cart_items(product_id, cart_id, quantity, item_value) VALUES('" + idString + "'," + cartId + "," + number + "," + itemsValue + ");";
                stmt.executeUpdate(sql);


                cartValue += itemsValue;


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Connect connect = new Connect();
            Statement stmt = connection.createStatement();

            String sql = "update carts set cart_value=" + cartValue + "where client_id=" + clientId + ";";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ImmutableTriple<String, Double, String> immutableTriple =
                new ImmutableTriple<>("done", cartValue, "rightValue");

        return immutableTriple;


    }

    //potrzebujhe do funkcji dodac indeks uzytkownika!!!!!
    public String buyCart(int clientId) {

        Set<Map.Entry<String, Integer>> s = cartProducts.entrySet();

        ImmutableTriple<String, Double, String> immutableTriple = saveCart(clientId);
        double cartValue = immutableTriple.getMiddle();

        try {
            Connect connect = new Connect();

            Statement stmt = connection.createStatement();

            String sql = "insert into order_details(client_id, created_date, order_value, status)" +
                    " values (" + clientId + ", current_timestamp()," + cartValue + ", 'created');";
            stmt.executeUpdate(sql);


            for (Map.Entry<String, Integer> it : s) {
                String productName = it.getKey();
                Integer number = it.getValue();

                Products products = new Products(connect.makeConnection(), productName);

                PreparedStatement selectAllSt3 = connection.prepareStatement("select id from order_details where client_id=" + clientId + ";");
                ResultSet orderTempId = selectAllSt3.executeQuery();
                orderTempId.next();
                String orderId = orderTempId.getString(1);

//                PreparedStatement selectAllSt4 = connection.prepareStatement("select price from products where name=" + productName + ";");
//                ResultSet price = selectAllSt4.executeQuery();
//                orderTempId.next();
//                String priceFinall = price.getString(1);

                double itemsValue = number * products.getPrice();


                String sql1 = "INSERT INTO order_items(product_id, order_id, quantity, items_value) VALUES('" + products.getId() + "','" + orderId + "'," + number + "," + itemsValue + ");";
                stmt.executeUpdate(sql1);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "done";
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
        System.out.println("");

    }

    public static String returnResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                return (columnValue);
            }
            return ("");
        }
        return ("");

    }

}