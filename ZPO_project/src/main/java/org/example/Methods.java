package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Methods {
    public String logIn() {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj login");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło");
        String password = scanner.nextLine();

        ResultSet user = onlineShop.getUser(login, password);
        String u = null;
        try {
            u = onlineShop.getResult(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return u;
//        try {
//            onlineShop.printResultSet(user);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void userBuyer(String user_type) {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        if("buyer".equals(user_type)) {
            try {
                System.out.println("\nLista dostępnych kategorii:");
                onlineShop.printResultSet(onlineShop.getAllCategories());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Użytkownik nie jest kupującym");
        }
    }

}
