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
        try {
            return onlineShop.getResult(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void userBuyer(String user_type) {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        if("buyer".equals(user_type)) {
            try {
//                System.out.println("\nLista dostępnych kategorii:");
                onlineShop.printResultSet(onlineShop.getAllCategories(), "\nLista dostępnych kategorii:");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Użytkownik nie jest kupującym");
        }
    }

    public String chooseCategory() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWybierz kategorię");
        return scanner.nextLine();
    }

    public void printProductsFromCategory() {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        try {
//            System.out.println("\nProdukty z kategorii " + category + ":");
            onlineShop.printResultSet(onlineShop.getAllProducts(), "\nProdukty z wybranej kategorii:");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String chooseProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWybierz produkt");
        return scanner.nextLine();
    }

    public void printProductInfo() {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        try {
            onlineShop.printProductDescription(onlineShop.getProductInfo(), "\nOpis wybranego produktu:");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void buyProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nCzy dodać produkt do koszyka? (t/n)");
        String respond = scanner.nextLine();
        while(!"t".equals(respond) && !"n".equals(respond)) {
            System.out.println("Spróbuj ponownie. Czy dodać produkt do koszyka? (t/n)");
            respond = scanner.nextLine();
        }
        if("t".equals(respond)) {
            System.out.println("Określ ilość");
            int amount = scanner.nextInt();
            //dodanie produktu do koszyka
        }
    }
}
