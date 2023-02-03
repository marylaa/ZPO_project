package org.example;

import java.sql.*;

public class Connect {

    public static Connection makeConnectionUser() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=user_login"); // nazwa uzytkownika
        urlSB.append("&password=password"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        return DriverManager.getConnection(connectionUrl); //połączenie z bazą
    }

    public static Connection makeConnectionBuyer() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=buyer"); // nazwa uzytkownika
        urlSB.append("&password=password"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        return DriverManager.getConnection(connectionUrl); //połączenie z bazą
    }

    public static Connection makeConnectionSeller() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=seller"); // nazwa uzytkownika
        urlSB.append("&password=password"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        return DriverManager.getConnection(connectionUrl); //połączenie z bazą
    }
}

