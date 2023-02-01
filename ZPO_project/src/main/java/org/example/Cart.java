package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cart {
    private Connection connection;

    public void checkOrdersHistory(int clientId) throws ClassNotFoundException {
        try {
            DatabaseContext onlineShop = new DatabaseContext(Connect.makeConnection());

            System.out.println("Imię i nazwisko klienta:");
            PreparedStatement function = connection.prepareStatement("select first_name, last_name from users where id=" + clientId + ";");
            onlineShop.printResultSet(function.executeQuery(), null);

            PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, order_value from order_details where client_id=" + clientId + ";");
            ResultSet orderDetails = selectAllSt.executeQuery();

            while(orderDetails.next()) {
                //orderDetails.next();
                int orderId = orderDetails.getInt(1);
                //printResultSet(orderDetails);
                String date = orderDetails.getString(2);
                String value1 = orderDetails.getString(3);

                System.out.println("Szczegóły zamówienia: \nid zamówienia, data utworzenia, wartość zamówienia [zł]");
                System.out.println(orderId + " " + date + " " + value1);

                System.out.println("Zamówione produkty: ");
                PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from order_items where order_id='" + orderId + "';");
                ResultSet orderedItems = selectAllSt1.executeQuery();

                while(orderedItems.next()) {

                    String productsId = orderedItems.getString(1);
                    int quantity = orderedItems.getInt(3);
                    double value = orderedItems.getInt(4);

                    System.out.println(value + " zł");
                    System.out.println(quantity + " szt.");

                    PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productsId + "';");
                    onlineShop.printResultSet(selectAllSt5.executeQuery(), null);
                }
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("aaaaa");;
        }
    }
}
