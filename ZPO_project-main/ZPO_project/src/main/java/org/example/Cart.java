package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.tuple.ImmutableTriple;


public class Cart {
    /**
     * Klasa reprezentująca koszyk.
     */

    private Connection connection;



    private int clientId;
    private Map<Products, Integer> cartProducts = new HashMap<Products, Integer>();

    /**
     * Metoda tworząca koszyk.
     *
     * @param conn - połączenie z bazą
     * @param clientId - ID klienta
     *
     */
    public Cart(Connection conn, int clientId) {
        connection = conn;
        try {
            Connect connect = new Connect();
            PreparedStatement selectAllSt = connection.prepareStatement("select id from carts where client_id='" + clientId + "';");
            ResultSet rsAllSt = selectAllSt.executeQuery();


            if (rsAllSt.next()) {

                int cartId = rsAllSt.getInt(1);
                PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, quantity from cart_items where cart_id='" + cartId + "';");
                ResultSet rsAllSt1 = selectAllSt1.executeQuery();

                while (rsAllSt1.next()) {
                    String productId = rsAllSt1.getString(1);
                    int productQuantity = rsAllSt1.getInt(2);



                    PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productId + "';");
                    ResultSet name = selectAllSt5.executeQuery();
                    while(name.next()) {
                        String productName = name.getString(1);

                        Products products = new Products(connect.makeConnection(), productName);
                        cartProducts.put(products, productQuantity);
                    }
                }

            } else {
                //System.out.println("puste");

            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Metoda dodająca produkt do koszyka.
     *
     * @param productName - nazwa produktu
     * @param number - liczba produktu
     *
     */
    public String addProduct(String productName, int number) {
        Connect connect = new Connect();


        Products products = new Products(connect.makeConnection(), productName);



        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();
        boolean inDict = false;
        for (Map.Entry<Products, Integer> it : s) {

            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                String bo = changeProductValueMore(productName,number);

                if(bo.equals("false")){
                    return "brak";
                }else{
                    inDict = true;
                    return "dodano";
                }

            }

        }
        if(!inDict){
            if (products.checkAvailability() >= number) {
                cartProducts.put(products, number);
                return "dodano";
            } else {
                return "brak";
            }
        }
        return "brak";


    }

    /**
     * Metoda usuwająca liczbę produktów w koszyku.
     *
     * @param productName - nazwa produktu
     * @param toDelete - liczba produktów do usunięcia
     *
     */
    public String changeProductValueLess(String productName, int toDelete) {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            Integer number = it.getValue();
            int newNumber = number - toDelete;
            if(newNumber >= 0) {
                if (productNameMap.getName().equals(productName)) {

                    cartProducts.replace(productNameMap, newNumber);

                }
            }else{
                System.out.println("Niewłaściwa liczba");
            }

        }

        return "done";
    }

    /**
     * Metoda dodająca liczbę produktów w koszyku.
     *
     * @param productName - nazwa produktu
     * @param toAdd - liczba produktów do dodania
     *
     */
    public String changeProductValueMore(String productName, int toAdd) {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();
        Connect connect = new Connect();
        Products products = new Products(connect.makeConnection(), productName);


        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                Integer number = it.getValue();
                int newNumber = number + toAdd;
                if (products.checkAvailability() >= newNumber) {
                    cartProducts.replace(productNameMap,newNumber);
                } else {
                    return "false";
                }

            }


        }
        return "done";
    }


    /**
     * Metoda usuwajaca jedną liczbę produktu.
     *
     * @param productName - produkt którego chcemy usunąć 1 liczbę
     *
     */
    public String deleteOneProduct(String productName) {

        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if(productNameMap.getName().equals(productName)){
                cartProducts.remove(productName);

            }

        }


        return "done";
    }

    public String clearCart() {
        cartProducts.clear();
        return "done";
    }

    public void showCart() {


        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        if(s.isEmpty()){
            System.out.println("Koszyk jest pusty.");
        }

        for (Map.Entry<Products, Integer> it : s) {
            Products productName = it.getKey();
            Integer number = it.getValue();
            System.out.println(productName);
            System.out.println("Produkt: " + productName.getName() + ", liczba: " + number);

        }
    }


    /**
     * Metoda sprawdzająca historię zamówień.
     *
     * @param clientId - ID klienta
     *
     */
    public String checkOrdersHistory(int clientId){


        try {
            Connect connect = new Connect();

            int counter = 1;


            System.out.println("HISTORIA ZAMÓWIEŃ");
            System.out.println("----------------------------------------------------------------------------");

            PreparedStatement selectAllSt2 = connection.prepareStatement("select first_name, last_name from users where id=" + clientId + ";");
            ResultSet client = selectAllSt2.executeQuery();
            while(client.next()){
                String clientString = client.getString(1);
                String clientString2 = client.getString(2);

                System.out.printf("| %30s |%n","IMIĘ I NAZWISKO KLIENTA: " + clientString + " " + clientString2);

            }
//            printResultSet(client);
            System.out.println("----------------------------------------------------------------------------");




            PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, order_value from order_details where client_id='" + clientId + "';",ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet orderDetails = selectAllSt.executeQuery();

            if(orderDetails.next()) {

                orderDetails.beforeFirst();
                while (orderDetails.next()) {
                    //orderDetails.next();
                    int orderId = orderDetails.getInt(1);
                    //printResultSet(orderDetails);
                    String date = orderDetails.getString(2);
                    String value1 = orderDetails.getString(3);

                    System.out.printf( "| %10s |%n", "ZAMÓWIENIE NR " + counter);

                    System.out.printf("| %4s | %19s | %10s |%n", "ID", "DATA UTWORZENIA", "WARTOŚĆ ZAMÓWIENIA [ZŁ]");
//                    System.out.println(orderId + " " + date + " " + value1);
                    System.out.printf("| %4s | %10s | %23s |%n",orderId, date, value1);

                    System.out.println(" ");
                    System.out.printf( "| %16s |%n", "ZAMÓWIONE PRODUKTY");

                    PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from order_items where order_id='" + orderId + "';");
                    ResultSet orderedItems = selectAllSt1.executeQuery();

                    while (orderedItems.next()) {

                        String productsId = orderedItems.getString(1);
                        int quantity = orderedItems.getInt(3);
                        double value = orderedItems.getInt(4);


                        PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productsId + "';");
                        ResultSet name = selectAllSt5.executeQuery();
                        while(name.next()) {
//                        printResultSet(name);
                            String nameString = name.getString(1);

                            System.out.printf("| %17s | %8s | %10s | %10s |%n", "PRODUKT", "ID PRODUKTU", "LICZBA", "WARTOŚĆ ZAMÓWIENIA [ZŁ]");

                            System.out.printf("| %17s | %11s | %10s | %23s |%n", nameString, productsId, quantity, value);

                            System.out.println("\n----------------------------------------------------------------------------");


                        }
                        counter += 1;
                    }

                }
            }else{
                System.out.println("Brak zamówień");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Brak zamówień");
        }

        return "done";

    }

//    public void checkOrdersHistory(int clientId){
//        try {
//            System.out.println("\nHistoria zamówień klienta:");
//            PreparedStatement selectAllSt2 = connection.prepareStatement("select concat(first_name, ' ', last_name) from users where id=" + clientId + ";");
//            ResultSet client = selectAllSt2.executeQuery();
//            printResultSet(client);
//
//            PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, order_value from order_details where client_id='" + clientId + "';");
//            ResultSet orderDetails = selectAllSt.executeQuery();
//            boolean notEmpty = orderDetails.next();
//
//            if(!notEmpty){
//                System.out.println("Pusta historia zamówień.");
//            }else {
//                while (orderDetails.next()) {
//                    int orderId = orderDetails.getInt(1);
//                    String date = orderDetails.getString(2);
//                    String value1 = orderDetails.getString(3);
//
//                    System.out.println("Szczegóły zamówienia:");
//                    System.out.println("id zamówienia, data utworzenia, wartość zamówienia [zł]");
//                    System.out.println(orderId + " " + date + " " + value1);
//
//                    System.out.println("Zamówione produkty: ");
//                    PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from order_items where order_id='" + orderId + "';");
//                    ResultSet orderedItems = selectAllSt1.executeQuery();
//
//                    while (orderedItems.next()) {
//
//                        String productsId = orderedItems.getString(1);
//                        int quantity = orderedItems.getInt(3);
//                        double value = orderedItems.getInt(4);
//
//                        System.out.println(value + " zł");
//                        System.out.println(quantity + " szt.");
//
//                        PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productsId + "';");
//                        ResultSet name = selectAllSt5.executeQuery();
//                        printResultSet(name);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Metoda zapisująca koszyk.
     *
     * @param clientId - ID klienta
     */
    public double saveCart(int clientId) {


        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();



        try {
            Connect connect = new Connect();
            Statement stmt = connection.createStatement();

            PreparedStatement selectAllSt2 = connection.prepareStatement("select id from carts order by id desc limit 1");
            ResultSet cartToDelete = selectAllSt2.executeQuery();

            while(cartToDelete.next()) {
                int cartToDeleteInt = cartToDelete.getInt(1);

                String sql00 = "delete from cart_items where cart_id='" + cartToDeleteInt + "';";
                stmt.executeUpdate(sql00);

                String sql0 = "delete from carts where id='" + cartToDeleteInt + "';";
                stmt.executeUpdate(sql0);

            }





            String sql = "INSERT INTO carts(client_id) VALUES(" + clientId + ");";
            stmt.executeUpdate(sql);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        double cartValue = 0;

        for (Map.Entry<Products, Integer> it : s) {
            Products product = it.getKey();
            String productName = product.getName();
            Integer number = it.getValue();

            try {
                Connect connect = new Connect();
                Statement stmt = connection.createStatement();



                String idString = product.getId();
                double priceInt = product.getPrice();



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

//        ImmutableTriple<String, Double, String> immutableTriple =
//                new ImmutableTriple<>("done", cartValue, "rightValue");
//
//        return immutableTriple;

        return cartValue;


    }

    /**
     * Metoda powodująca kupienie zawartości koszyka.
     *
     * @param clientId - ID klienta
     *
     */
    public String buyCart(int clientId) {

        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

//        ImmutableTriple<String, Double, String> immutableTriple = saveCart(clientId);
//        double cartValue = immutableTriple.getMiddle();


        double cartValue = saveCart(clientId);




        try {
            Connect connect = new Connect();

            Statement stmt = connection.createStatement();
            inProductStats stats = new inProductStats(connect.makeConnection());


            String sql = "insert into order_details(client_id, created_date, order_value, status)" +
                    " values (" + clientId + ", current_timestamp()," + cartValue + ", 'created');";
            stmt.executeUpdate(sql);


            for (Map.Entry<Products, Integer> it : s) {
                Products product = it.getKey();
                Integer number = it.getValue();

                stats.addToProductStats(product.getId());

                PreparedStatement selectAllSt1 = connection.prepareStatement("select id from product_stats where product_id='" + product.getId() + "';");
                ResultSet rs1 = selectAllSt1.executeQuery();
                if(rs1.next()) {

                    int productStatsId = rs1.getInt(1);

                    PreparedStatement selectAllSt2 = connection.prepareStatement("select purchased_quantity from product_stats where product_id='" + productStatsId + "';");
                    ResultSet rs2 = selectAllSt2.executeQuery();
                    if(rs2.next()){
                        int quantity = rs2.getInt(1);
                        String sql9 = "update product_stats set purchased_quantity=" + quantity + "+" + number + " where id='" + productStatsId + "';";
                        stmt.executeUpdate(sql9);
                    }else {
                        int quantity = 0;

                        String sql9 = "update product_stats set purchased_quantity=" + quantity + "+" + number + " where id='" + productStatsId + "';";
                        stmt.executeUpdate(sql9);
                    }

                }


                PreparedStatement selectAllSt3 = connection.prepareStatement("select id from order_details order by id desc limit 1;");
                ResultSet orderTempId = selectAllSt3.executeQuery();
                while(orderTempId.next()) {
                    String orderId = orderTempId.getString(1);


                    double itemsValue = number * product.getPrice();


                    String sql1 = "INSERT INTO order_items(product_id, order_id, quantity, items_value) VALUES('" + product.getId() + "','" + orderId + "'," + number + "," + itemsValue + ");";
                    stmt.executeUpdate(sql1);

                    PreparedStatement selectAllSt6 = connection.prepareStatement("select id from carts where client_id='" + clientId + "';");
                    ResultSet cartId = selectAllSt6.executeQuery();
                    while (cartId.next()) {
                        int cart = cartId.getInt(1);

                        String sql7 = "delete from cart_items where cart_id=" + cart + ";";
                        stmt.executeUpdate(sql7);
                    }

                    String sql5 = "delete from carts where client_id='" + clientId + "';";
                    stmt.executeUpdate(sql5);

                    String sql8 = "update products set availability=availability-" + number + " where id='" + product.getId() + "';";
                    stmt.executeUpdate(sql8);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "done";
    }

    /**
     * Metoda drukująca dane wynikowe z bazy danych.
     *
     * @param resultSet - zapytanie do bazy danych
     *
     */
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

    /**
     * Metoda zwracająca dane wynikowe z bazy danych.
     *
     * @param resultSet - zapytanie do bazy danych
     *
     */
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