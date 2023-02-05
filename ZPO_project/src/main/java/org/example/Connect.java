package org.example;

import java.sql.*;

public class Connect {

    /**
     * Bezparametrowy konstruktor.
     */
    public Connect() {
    }

    /**
     * Metoda realizująca połączenie z bazą danych.
     *
     * @param userName nazwa użytkownika
     * @return połączenie z bazą
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection makeConnection(String userName) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        StringBuilder urlSB = new StringBuilder("jdbc:mysql://"); //polaczenie z MySQL
        urlSB.append("localhost:3306/"); // numer portu
        urlSB.append("online_shop?"); // nazwa bazy
        urlSB.append("useUnicode=true&characterEncoding=utf-8"); // kodowanie
        urlSB.append("&user=" + userName); // nazwa uzytkownika
        urlSB.append("&password=password"); // haslo uzytkownika
        urlSB.append("&serverTimezone=CET"); // strefa czasowa
        String connectionUrl = urlSB.toString();

        return DriverManager.getConnection(connectionUrl); //połączenie z bazą
    }
}

