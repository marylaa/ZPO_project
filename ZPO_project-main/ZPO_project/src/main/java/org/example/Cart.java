package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public String addProduct(String productName, int number){
        try {
            Connect connect = new Connect();
            PreparedStatement selectAllSt = connection.prepareStatement("select availability from products where name like'" + productName + "';");
            ResultSet rsAllSt = selectAllSt.executeQuery();

            boolean bo = rsAllSt.next();


            if (bo) {
                long value = rsAllSt.getLong(1);
                if(value >= number) {
                    cartProducts.put(productName, number);
                    return "dodano";
                }else{
                    return "brak";
                }
            }else{
                return "zły";
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String changeProductValueLess(String productName){
        int number = cartProducts.get(productName);
        cartProducts.replace(productName,number - 1);
        return "done";
    }

    public String changeProductValueMore(String productName){
        int number = cartProducts.get(productName);
        cartProducts.replace(productName,number + 1);
        return "done";
    }

    public String deleteOneProduct(String productName){
        cartProducts.remove(productName);
        return "done";
    }

    public String clearCart(){
        cartProducts.clear();
        return "done";
    }

    public void showCart(){
        System.out.println(cartProducts);
    }

    public void deleteCart(){

    }

    //Map cartProducts

    //potrzebujhe do funkcji dodac indeks uzytkownika!!!!!
    public String saveCart(int clientId){


        Set<Map.Entry<String,Integer>> s = cartProducts.entrySet();
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

        for(Map.Entry<String, Integer> it: s) {
            String productName = it.getKey();
            Integer number = it.getValue();

            //System.out.println(productName);
            try {
                Connect connect = new Connect();
                Statement stmt = connection.createStatement();

                PreparedStatement selectAllSt1 = connection.prepareStatement("select id from products where name like'" + productName + "';");
                ResultSet productId = selectAllSt1.executeQuery();
                productId.next();
                //printResultSet(productId);
                String idString = productId.getString(1);


                PreparedStatement selectAllSt2 = connection.prepareStatement("select price from products where name like'" + productName + "';");
                ResultSet price = selectAllSt2.executeQuery();
                price.next();
                double priceInt = price.getDouble(1);

                double itemsValue = number * priceInt;

                System.out.println( idString + " " + priceInt + " " + itemsValue);


                PreparedStatement selectAllSt3 = connection.prepareStatement("select id from carts order by id desc  limit 1;");
                ResultSet cartTempId = selectAllSt3.executeQuery();
                cartTempId.next();
                String cartId = cartTempId.getString(1);
                //System.out.println(cartId);

                //.out.println("Inserting records into the table...");
                String sql = "INSERT INTO cart_items(product_id, cart_id, quantity, item_value) VALUES('"+ idString + "'," + cartId + "," + number + "," + itemsValue + ");";
                stmt.executeUpdate(sql);


                cartValue += itemsValue;




            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Connect connect = new Connect();
            Statement stmt = connection.createStatement();

            String sql = "update carts set cart_value="+cartValue + "where client_id="+ clientId + ";";
            stmt.executeUpdate(sql);

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
                return(columnValue);
            }
            return("");
        }
        return("");

    }

}