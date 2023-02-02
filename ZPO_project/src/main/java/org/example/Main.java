package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.startMenu();
        } catch (SQLException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Przepraszamy. Wystąpił błąd.");
        }
    }
}