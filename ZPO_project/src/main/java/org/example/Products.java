package org.example;

import java.sql.*;

public class Products {
    /**
     * Klasa reprezentująca produkt.
     */
    private Connection connection;
    private String id;
    private String categoryId;
    private String name;
    private String producer;
    private String description;
    private double price;
    private Date addedDate;
    private String userId;
    private int availability;

    /**
     * Metoda pobierająca informacje na temat produktu z bazy danych.
     *
     * @param productName - nazwa produktu
     */
    public Products(Connection connection, String productName) throws ClassNotFoundException, SQLException {
        this.connection = connection;

        PreparedStatement selectAllSt = connection.prepareStatement("select id, category_id, producer, description, price, added_date, user_id, availability from products where name='" + productName + "';");
        ResultSet rs = selectAllSt.executeQuery();

        while (rs.next()) {
            id = rs.getString(1);
            categoryId = rs.getString(2);
            producer = rs.getString(3);
            description = rs.getString(4);
            price = rs.getInt(5);
            addedDate = rs.getDate(6);
            userId = rs.getString(7);
            availability = rs.getInt(8);
            name = productName;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailability() {
        return availability;
    }
}