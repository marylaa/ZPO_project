package org.example;

import java.sql.*;

public class Connect {

    public static Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=root"); // nazwa uzytkownika
        urlSB.append("&password=aresik"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        return DriverManager.getConnection(connectionUrl); //połączenie z bazą
    }
}

