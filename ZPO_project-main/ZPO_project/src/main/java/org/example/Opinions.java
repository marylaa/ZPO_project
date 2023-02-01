package org.example;

import java.sql.*;

import static org.example.Cart.printResultSet;

public class Opinions {
    /**
     * Klasa reprezentująca opinie.
     *
     */

    private Connection connection;

    public Opinions(Connection conn) {
        connection = conn;
    }

    /**
     * Metoda dodająca opinię o produkcie.
     *
     * @param  clientId - ID klienta
     * @param productName - nazwa produktu
     * @param rating - ocena
     * @param text - treść opinii
     *
     */
    public String addOpinion(int clientId,String productName, String text,double rating){



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
                    inProductStats stats = new inProductStats(connect.makeConnection());

                    stats.addToProductStats(products.getId());
//
                    PreparedStatement selectAllSt1 = connection.prepareStatement("select id, rating from product_stats where product_id='" + products.getId() + "';");
                    ResultSet rs1 = selectAllSt1.executeQuery();
                    if(rs1.next()) {

                        int productStatsId = rs1.getInt(1);
                        double productStatsRating = rs1.getInt(2);

                        String sql1 = "INSERT INTO product_opinions(product_stat_id, client_id, description) values('" + productStatsId + "','" + clientId + "','" + text + "');";
                        stmt.executeUpdate(sql1);

                        if(productStatsRating == 0){
                            double finalRating = rating;
                            String sql9 = "update product_stats set rating=" + finalRating + " where id='" + productStatsId + "';";
                            stmt.executeUpdate(sql9);
                            return "done";
                        }else if(productStatsRating != 0) {
                            double finalRating = (productStatsRating + rating) / 2;
                            String sql9 = "update product_stats set rating=" + finalRating + " where id='" + productStatsId + "';";
                            stmt.executeUpdate(sql9);
                            return "done";
                        }


                    }
//                    String sql = "INSERT INTO product_stats(product_id) value('" + products.getId() + "');";
//                    stmt.executeUpdate(sql);
//
//
//                    PreparedStatement selectAllSt4 = connection.prepareStatement("select id from product_stats where product_id='" + products.getId() + "';");
//                    ResultSet rs4 = selectAllSt4.executeQuery();
//                    if(rs4.next()) {
//                        int productStatsId = rs4.getInt(1);
//
//
//                        String sql5 = "INSERT INTO product_opinions(product_stat_id, client_id, description) values('" + productStatsId + "','" + clientId + "','" + text + "');";
//                        stmt.executeUpdate(sql5);
//                    }

                }else{
                    return "not buyer";
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "done";
    }


    /**
     * Metoda drukująca opinie o produkcie.
     *
     * @param productName - nazwa produktu
     */
    public String showOpinions(String productName){


        try {
            Connect connect = new Connect();
            connect.makeConnection();
            Statement stmt = connection.createStatement();

            Products products = new Products(connect.makeConnection(), productName);



            PreparedStatement selectAllSt1 = connection.prepareStatement("select rating, id from product_stats where product_id='" + products.getId() + "' limit 1;");
            ResultSet rs1 = selectAllSt1.executeQuery();
            while (rs1.next()) {
                double rating = rs1.getDouble(1);
                String productStatID = rs1.getString(2);
                System.out.println("Ocena (skala 1 - 5):");
                System.out.println(rating);

                System.out.println("Opinie o produkcie " + productName);
                PreparedStatement selectAllSt = connection.prepareStatement("select description from product_opinions where product_stat_id='" + productStatID + "';");
                ResultSet rs = selectAllSt.executeQuery();
                printResultSetEnumerate(rs);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "done";



    }

    /**
     * Metoda drukująca ponumerowane dane wynikowe z bazy danych.
     *
     * @param resultSet - zapytanie do bazy danych
     *
     */
    public static void printResultSetEnumerate(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        int counter = 1;
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print("Opinia " + counter + ": " + columnValue);
                counter += 1;
            }
            System.out.println("");
        }
        System.out.println("");

    }
}

