package org.example;

import java.sql.*;

public class InProductStats {
    /**
     * Klasa odpowiadająca za statystyki produkt.
     *
     */
    private Connection connection;

    public InProductStats() throws SQLException, ClassNotFoundException {
        this.connection = Connect.makeConnection();
    }

    /**
     * Metoda dodajaca produkt do tabeli product_stats w bazie danych
     *
     * @param productId - ID produktu
     *
     */
    public String addToProductStats(String productId) throws ClassNotFoundException {
        try {
            Statement stmt = connection.createStatement();

            PreparedStatement selectAllSt1 = connection.prepareStatement("select id from product_stats where product_id='" + productId + "';");
            ResultSet rs1 = selectAllSt1.executeQuery();
            if (rs1.next()) {
                return "exist";

            }
            String sql10 = "INSERT INTO product_stats(product_id, purchased_quantity, rating) value('" + productId + "'," + "0, 0.0" + ");";
            stmt.executeUpdate(sql10);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "done";
    }
}