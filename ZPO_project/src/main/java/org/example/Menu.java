package org.example;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private static String userType;
    private static int userId;
    private static Connect connect;
    private static DatabaseContext onlineShop;
    private static Seller seller;
    private static Buyer buyer;

    public Menu() {
        this.connect = new Connect();
        this.onlineShop = new DatabaseContext(connect.makeConnection());
    }

    public void startMenu() {
        if(userId == 0) {
            System.out.println("\nSklep internetowy");
            login();
        }

        if ("buyer".equals(userType)) {
            String action = getInput("Co chcesz zrobić? \n1 - wyświetlić listę kategorii \n2 - sprawdzić historię zamówień \n3 - sprawdzić zawartość koszyka \n4 - wylogować");
            while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action)) {
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                action = getInput("\nPodaj numer");
            }
            switch (action) {
                case "1":
                    buyer.printCategories();
                    break;
                case "2":
                    //HISTORIA ZAMOWIEN
                    break;
                case "3":
                    //KOSZYK
                    break;
                case "4":
                    logout();
                    break;
            }
        } else {
            String action = getInput("Co chcesz zrobić? \n1 - wyświetlić listę swoich produktów \n2 - dodać nowe produkty \n3 - wylogować");
            while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action)) {
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                action = getInput("\nPodaj numer");
            }
            switch (action) {
                case "1":
                    seller.showAndEditProducts(userId);
                    break;
                case "2":
                    seller.sellerAddProducts(userId);
                    startMenu();
                    break;
                case "3":
                    logout();
                    break;
            }
        }
    }


    public String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n" + prompt);
        return scanner.nextLine();
    }

    public void login() {
        this.buyer = new Buyer();
        this.seller = new Seller();

        String id = onlineShop.getUserId(getInput("Podaj login"), getInput("Podaj hasło"));
        while (id == null) {
            System.out.println("\nLogowanie nie powiodło się. Spróbuj ponownie.");
            id = onlineShop.getUserId(getInput("Podaj login"), getInput("Podaj hasło"));
        }
        this.userId = Integer.valueOf(id);
        this.userType = onlineShop.getUserType(userId);
    }

    public void logout() {
        String action = getInput("Czy na pewno chcesz się wylogować? (tak/nie)");
        while (!"tak".equals(action) && !"nie".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = getInput("Czy na pewno chcesz się wylogować? (tak/nie)");
        }
        switch (action) {
            case "tak":
                this.userId = 0;
                this.userType = null;
                startMenu();
                break;
            case "nie":
                startMenu();
                break;
        }
    }

    public void printResultSet(ResultSet resultSet, String description) throws SQLException {
        System.out.println(description);
        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) { // wartosci w rzedach
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }


    public void printProductDescription(ResultSet resultSet, String description) throws SQLException {
        System.out.println(description);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] column = {"nazwa produktu", "producent", "opis", "cena (w zł)", "sprzedający", "dostępność (w sztukach)"};
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print("\n");
                String columnValue = resultSet.getString(i);
                System.out.print(column[i - 1] + " - " + columnValue);
            }
            System.out.println("");
        }
    }
}
