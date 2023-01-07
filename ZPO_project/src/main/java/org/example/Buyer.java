package org.example;

import java.sql.SQLException;

public class Buyer {
    private Connect connect;
    private DatabaseContext onlineShop;
    private Menu menu;

    public Buyer() {
        this.connect = new Connect();
        this.onlineShop = new DatabaseContext(connect.makeConnection());
        this.menu = new Menu();
    }

    public void printCategories() {
        try {
            menu.printResultSet(onlineShop.getAllCategories(), "\nLista dostępnych kategorii:");

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
            menu.printResultSet(onlineShop.getAllProducts(categoryId), "\nProdukty z wybranej kategorii:");

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
                    //SORTOWANIE PRODUKTÓW
                    break;
                case "3":
                    printCategories();
                    break;
                case "4":
                    menu.startMenu();
                    break;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void printProductInfo(String productId, String categoryId) {
        try {
            menu.printProductDescription(onlineShop.getProductInfo(productId), "\nOpis wybranego produktu:");

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
                    //SPRAWDZENIE ILOSCI SPRZEDANYCH
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

}
