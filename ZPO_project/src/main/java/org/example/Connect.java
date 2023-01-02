package org.example;

import java.sql.*;

public class Connect {
    public Connection makeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=root"); // nazwa uzytkownika
        urlSB.append("&password=aresik"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        try {
            Connection conn = DriverManager.getConnection(connectionUrl); //połączenie z bazą
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) { // wyswietlenie nazw kolumn i wartosci w rzedach
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
            }
            System.out.println("");
        }
        System.out.println("");
    }
}

