package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Buyer {
    private DatabaseContext onlineShop;
    private Menu menu;
    private Cart cart;

    public Buyer(Cart cart) throws SQLException, ClassNotFoundException {
        this.onlineShop = new DatabaseContext(Connect.makeConnection());
        this.menu = new Menu();
        this.cart = cart;
    }

    public void printCategories() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
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
    }

    public void printProductsFromCategory(String categoryId) throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        onlineShop.printResultSet(onlineShop.getAllProducts(categoryId), "\nProdukty z wybranej kategorii:");
        productsOperations(categoryId);
    }

    public void productsOperations(String categoryId) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String action = menu.getInput("Co chcesz zrobić? \n1 - wyświetlić informacje o danym produkcie \n2 - posortować produkty \n3 - wrócić do wyboru kategorii \n4 - wrócić do menu");
        while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("Podaj numer");
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

    public void sortProducts(String categoryId) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    public void printProductInfo(String productId, String categoryId) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        printProductDescription(onlineShop.getProductInfo(productId), "\nOpis wybranego produktu:");
        afterProductInfoOperations(productId, categoryId);
    }

    private void afterProductInfoOperations(String productId, String categoryId) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String action = menu.getInput("Co chcesz zrobić? \n1 - dodać produkt do koszyka \n2 - przeczytać opinie \n3 - dodać opinie \n4 - sprawdzić ilość sprzedanych sztuk \n5 - wrócić do wyboru produktu \n6 - wrócić do menu");
        while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action) && !"5".equals(action) && !"6".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        Opinions opinions = new Opinions();
        switch (action) {
            case "1":
                addProductToCart(productId);
                afterProductInfoOperations(productId, categoryId);
                break;
            case "2":
                opinions.showOpinions(productId);
                afterProductInfoOperations(productId, categoryId);
                break;
            case "3":
                addUserOpinion(productId, opinions);
                menu.startMenu();
                break;
            case "4":
                System.out.println("\nIlość sprzedanych sztuk: " + onlineShop.checkPurchasedQuantityOfProduct(productId));
                afterProductInfoOperations(productId, categoryId);
                menu.startMenu();
                break;
            case "5":
                printProductsFromCategory(categoryId);
                break;
            case "6":
                menu.startMenu();
                break;
        }
    }

    private void addUserOpinion(String productId, Opinions opinions) throws ClassNotFoundException, SQLException {
        String opinionText = menu.getInput("Podaj treść:");
        double rating = -1.0;
        while (rating < 0 || rating > 5) {
            try {
                rating = Double.valueOf(menu.getInput("Podaj ocenę w skali od 1 do 5:"));
            } catch (IllegalArgumentException e) {
                System.out.println("Błąd. Podaj liczbę.");
            }
        }
        opinions.addOpinion(menu.getId(), productId, opinionText, rating);
    }

    private void addProductToCart(String productId) throws SQLException, ClassNotFoundException {
        int number = 0;
        while (number == 0) {
            try{
                number = Integer.valueOf(menu.getInput("Jaką ilość produktu chcesz dodać do koszyka?"));
            } catch (IllegalArgumentException e) {
                System.out.println("Błąd. Podaj liczbę.");
            }
        }
        cart.addProduct(productId, number);
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
