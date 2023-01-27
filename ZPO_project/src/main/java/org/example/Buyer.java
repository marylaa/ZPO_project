package org.example;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Buyer {
    private DatabaseContext onlineShop;
    private Menu menu;

    public Buyer() {
        this.onlineShop = new DatabaseContext(Connect.makeConnection());
        this.menu = new Menu();
    }

    public void printCategories() {
        try {
            onlineShop.printResultSet(onlineShop.getAllCategories(), "\nLista dostępnych kategorii:");

            String action = menu.getInput("Co chcesz zrobić? \n1 - wyświetlić produkty z danej kategorii \n2 - wrócić do menu");
            while (!"1".equals(action) && !"2".equals(action)) {
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                action = menu.getInput("\nPodaj numer");
            }
            switch (action) {
                case "1":
                    String categoryName = menu.getInput("Wybierz kategorię");
                    String categoryId = onlineShop.getCategoryId(categoryName);
                    while (categoryId == null) {
                        System.out.println("Niepoprawna nazwa kategorii. Spróbuj ponownie.");
                        categoryName = menu.getInput("Wybierz kategorię");
                        categoryId = onlineShop.getCategoryId(categoryName);
                    }
                    printProductsFromCategory(categoryId);
                    break;
                case "2":
                    menu.startMenu();
                    break;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void printProductsFromCategory(String categoryId) {
        try {
            onlineShop.printResultSet(onlineShop.getAllProducts(categoryId), "\nProdukty z wybranej kategorii:");
            productsOperations(categoryId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void productsOperations(String categoryId){
        String action = menu.getInput("Co chcesz zrobić? \n1 - wyświetlić informacje o danym produkcie \n2 - posortować produkty \n3 - wrócić do wyboru kategorii \n4 - wrócić do menu");
        while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        switch (action) {
            case "1":
                String productName = menu.getInput("Wybierz produkt");
                String productId = onlineShop.getProductId(productName);
                while (productId == null) {
                    System.out.println("Niepoprawna nazwa produktu. Spróbuj ponownie.");
                    productName = menu.getInput("Wybierz produkt");
                    productId = onlineShop.getProductId(productName);
                }
                printProductInfo(productId, categoryId);
                break;
            case "2":
                sortProducts(categoryId);
                break;
            case "3":
                printCategories();
                break;
            case "4":
                menu.startMenu();
                break;
        }
    }

    public void sortProducts(String categoryId) {
        String action = menu.getInput("Po czym chcesz posortować? \n1 - po cenie produktu \n2 - po dacie dodania produktu \n3 - po ocenie produktu \n4 - wrócić do wyboru kategorii \n5 - wrócić do menu");
        while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action) && !"5".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        switch (action) {
            case "1":
                onlineShop.getSortedProducts("price", howToSort(), categoryId);
                productsOperations(categoryId);
                break;
            case "2":
                onlineShop.getSortedProducts("added_date", howToSort(), categoryId);
                productsOperations(categoryId);
                break;
            case "3":
                onlineShop.getSortedProducts("rating", howToSort(), categoryId);
                productsOperations(categoryId);
                break;
            case "4":
                printCategories();
                break;
            case "5":
                menu.startMenu();
                break;
        }
    }

    private String howToSort() {
        String action = menu.getInput("Chcesz posortować rosnąco czy malejąco? \n1 - rosnąco \n2 - malejąco");
        while (!"1".equals(action) && !"2".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        switch (action) {
            case "1":
                return "ASC";
            case "2":
                return "DESC";
        }
        return null;
    }

    public void printProductInfo(String productId, String categoryId) {
        try {
            printProductDescription(onlineShop.getProductInfo(productId), "\nOpis wybranego produktu:");

            String action = menu.getInput("Co chcesz zrobić? \n1 - kupić produkt \n2 - przeczytać opinie \n3 - dodać opinie \n4 - sprawdzić ilość sprzedanych sztuk \n5 - wrócić do wyboru produktu \n6 - wrócić do menu");
            while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action) && !"5".equals(action) && !"6".equals(action)) {
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                action = menu.getInput("\nPodaj numer");
            }
            switch (action) {
                case "1":
                    //KUPNO PRODUKTU

                    //pozniej wyswietlenie kategorii
                    printCategories();
                    break;
                case "2":
                    //CZYTANIE OPINII
                    break;
                case "3":
                    //DODANIE OPINII
                    break;
                case "4":
                    System.out.println("\nIlość sprzedanych sztuk: " + onlineShop.checkPurchasedQuantityOfProduct(productId));
                    printProductInfo(productId, categoryId);
                    break;
                case "5":
                    printProductsFromCategory(categoryId);
                    break;
                case "6":
                    menu.startMenu();
                    break;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
