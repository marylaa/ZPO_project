package org.example;

import java.sql.*;

public class inProductStats {

    private Connection connection;

    public inProductStats(Connection conn) {
        connection = conn;
    }

    public String addToProductStats(String productId){



        try {

            Connect connect = new Connect();
            connect.makeConnection();
            Statement stmt = connection.createStatement();


            PreparedStatement selectAllSt1 = connection.prepareStatement("select id from product_stats where product_id='" + productId + "';");
            ResultSet rs1 = selectAllSt1.executeQuery();
            if (rs1.next()) {
                System.out.println("tu");

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
