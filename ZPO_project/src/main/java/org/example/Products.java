package org.example;

import com.mysql.cj.exceptions.WrongArgumentException;

import java.sql.*;

import static org.example.Cart.printResultSet;
import static org.example.Cart.returnResultSet;

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
     *
     */
    public Products(String productName) throws ClassNotFoundException, SQLException {
        this.connection = Connect.makeConnection();
        try {
            PreparedStatement selectAllSt = connection.prepareStatement("select id, category_id, producer, description, price, added_date, user_id, availability from products where name='" + productName + "';");
            ResultSet rs = selectAllSt.executeQuery();

            while(rs.next()) {
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
        } catch (SQLException e) {
            throw new WrongArgumentException("brak");
        }
    }

    /**
     * Metoda sprawdzająca dostepność produktu.
     *
     */
    public int checkAvailability(){
        int availability = getAvailability();
        return availability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Products{" +
                "connection=" + connection +
                ", id='" + id + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                ", producer='" + producer + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", addedDate=" + addedDate +
                ", userId='" + userId + '\'' +
                ", availability=" + availability +
                '}';
    }
}