package org.example;

import java.sql.SQLException;

public class Seller {
    private Connect connect;
    private DatabaseContext onlineShop;
    private Menu menu;

    public Seller() {
        this.connect = new Connect();
        this.onlineShop = new DatabaseContext(connect.makeConnection());
        this.menu = new Menu();
    }
    public void showAndEditProducts(int id) {
        try {
            menu.printResultSet(onlineShop.getSellerProducts(id), "\nLista twoich produktów:");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String action = menu.getInput("Co chcesz zrobić? \n1 - edytować produkt \n2 - wrócić");
        while (!"1".equals(action) && !"2".equals(action)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            action = menu.getInput("\nPodaj numer");
        }
        switch (action) {
            case "1":
                sellerEditProduct();
                //DOKONCZYC
                menu.startMenu();
                break;
            case "2":
                menu.startMenu();
                break;
        }
    }

    public void sellerEditProduct() {
        String productName = menu.getInput("Który produkt chcesz edytować?");
        String productId = onlineShop.getProductId(productName);
        while (productId == null) {
            System.out.println("Niepoprawna nazwa produktu. Spróbuj ponownie.");
            productName = menu.getInput("Wybierz produkt");
            productId = onlineShop.getProductId(productName);
        }
        onlineShop.editProduct(productId);
    }

    public void sellerAddProducts(int userId) {
        String id = menu.getInput("\nPodaj id produktu");
        String category = menu.getInput("Podaj id kategorii");
        String name = menu.getInput("Podaj nazwę produktu");
        String producer = menu.getInput("Podaj producenta");
        String description = menu.getInput("Podaj opis produktu");
        String price = menu.getInput("Podaj cenę (w zł)");
        double priceDouble = Double.valueOf(price);
        String availability = menu.getInput("Podaj dostępną liczbę sztuk");
        int availabilityInteger = Integer.valueOf(availability);
        onlineShop.addProduct(id, category, name, producer, description, priceDouble, userId, availabilityInteger);
    }
}
