package org.example;

import java.sql.*;

public class Opinions {

    private Connection connection;

    public Opinions(Connection conn) {
        connection = conn;
    }

    public String addOpinion(int clientId,String productName, String text){



        try {
            Connect connect = new Connect();
            connect.makeConnection();
            Statement stmt = connection.createStatement();

            PreparedStatement selectAllSt = connection.prepareStatement("select user_type from users where id='" + clientId + "';");
            ResultSet rs = selectAllSt.executeQuery();

            if(rs.next()){
                String userType = rs.getString(1);
                if(("buyer").equals(userType)){

                    Products products = new Products(connect.makeConnection(),productName);

                    PreparedStatement selectAllSt1 = connection.prepareStatement("select id from product_stats where product_id='" + products.getId() + "';");
                    ResultSet rs1 = selectAllSt1.executeQuery();
                    if(rs1.next()) {

                        int productStatsId = rs1.getInt(1);

                        String sql1 = "INSERT INTO product_opinions(product_stat_id, client_id, description) values('" + productStatsId + "','" + clientId + "','" + text + "');";
                        stmt.executeUpdate(sql1);
                        return "done";

                    }
                    String sql = "INSERT INTO product_stats(product_id) value('" + products.getId() + "');";
                    stmt.executeUpdate(sql);


                    PreparedStatement selectAllSt4 = connection.prepareStatement("select id from product_stats where product_id='" + products.getId() + "';");
                    ResultSet rs4 = selectAllSt4.executeQuery();
                    if(rs4.next()) {
                        int productStatsId = rs4.getInt(1);


                        String sql5 = "INSERT INTO product_opinions(product_stat_id, client_id, description) values('" + productStatsId + "','" + clientId + "','" + text + "');";
                        stmt.executeUpdate(sql5);
                    }

                }else{
                    return "not buyer";
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "done";
    }

}
