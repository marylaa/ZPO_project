package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Methods {
    public String[] logIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj login");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło");
        String password = scanner.nextLine();
        String[] data = {login, password};
        return data;
    }

//    public String logIn() {
//        Connect connect = new Connect();
//        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Podaj login");
//        String login = scanner.nextLine();
//        System.out.println("Podaj hasło");
//        String password = scanner.nextLine();
//
//        ResultSet user = onlineShop.getUser(login, password);
//        try {
//            return onlineShop.getResult(user);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void user() {
        Connect connect = new Connect();
        DatabaseContext onlineShop = new DatabaseContext(connect.makeConnection());
        int userId = onlineShop.getUser();
        String userType = onlineShop.getUserType(userId);
        if("buyer".equals(userType)) {
            userBuyer(onlineShop);
        } else {
            userSeller(onlineShop, userId);
        }
    }

    private void userBuyer(DatabaseContext onlineShop) {
        try {
            onlineShop.printResultSet(onlineShop.getAllCategories(), "\nLista dostępnych kategorii:");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void userSeller(DatabaseContext onlineShop, int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Co chcesz zrobić, podaj numer? \n1 - wyświetlić listę swoich produktów \n2 - edytować swoje produkty \n3 - dodać nowe produkty");
        int action = scanner.nextInt();
        while(action != 1 && action != 2 && action != 3) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            System.out.println("\nPodaj numer");
            action = scanner.nextInt();
        }
        switch (action) {
            case 1:
                //Wyświetlenie listy produktów
                onlineShop.printSellerProducts(userId);
                break;
            case 2:
                System.out.println("Edycja");
                break;
            case 3:
                System.out.println("Dodanie");
                break;
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
