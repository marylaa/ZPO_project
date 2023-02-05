package org.example;

import java.sql.*;

public class InProductStats {
    /**
     * Klasa odpowiadająca za statystyki produkt.
     */
    private Connection connection;

    /**
     * Bezparametrowy konstruktor.
     */
    public InProductStats() {
    }

    /**
     * Parametrowy konstruktor.
     *
     * @param connection obiekt klasy Connection
     */
    public InProductStats(Connection connection) {
        this.connection = connection;
    }

    /**
     * Metoda dodajaca produkt do tabeli product_stats w bazie danych.
     *
     * @param productId - id produktu
     */
    public void addToProductStats(String productId) throws SQLException {
        Statement stmt = connection.createStatement();

        String sql10 = "INSERT INTO product_stats(product_id, purchased_quantity, rating) value('" + productId + "'," + "0, 0.0" + ");";
        stmt.executeUpdate(sql10);
    }
}