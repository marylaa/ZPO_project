package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.sql.Statement;

public class Cart {
    /**
     * Klasa reprezentująca koszyk.
     */
    private Connection connection;
    private DatabaseContext onlineShop;
    private static Map<Products, Integer> cartProducts = new HashMap<>();

    /**
     * Metoda tworząca koszyk.
     *
     * @param clientId - ID klienta
     */
    public Cart(int clientId, DatabaseContext onlineShop, Connection connection) throws ClassNotFoundException, SQLException {
        this.connection = connection;
        this.onlineShop = onlineShop;

        Statement stmt = connection.createStatement();
        PreparedStatement selectAllSt = connection.prepareStatement("select id from carts where client_id='" + clientId + "';");
        ResultSet rsAllSt = selectAllSt.executeQuery();

        if (rsAllSt.next()) {
            int cartId = rsAllSt.getInt(1);
            PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, quantity from cart_items where cart_id='" + cartId + "';");
            ResultSet rsAllSt1 = selectAllSt1.executeQuery();

            while (rsAllSt1.next()) {
                String productId = rsAllSt1.getString(1);
                int productQuantity = rsAllSt1.getInt(2);

                PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productId + "';");
                ResultSet name = selectAllSt5.executeQuery();
                while (name.next()) {
                    String productName = name.getString(1);

                    Products products = new Products(connection, productName);
                    cartProducts.put(products, productQuantity);
                }
            }
        }

        PreparedStatement selectAllSt6 = connection.prepareStatement("select id from carts where client_id='" + clientId + "';");
        ResultSet cartId = selectAllSt6.executeQuery();
        while (cartId.next()) {
            int cart = cartId.getInt(1);

            String sql7 = "delete from cart_items where cart_id=" + cart + ";";
            stmt.executeUpdate(sql7);
        }
        String sql9 = "delete from carts where client_id='" + clientId + "';";
        stmt.executeUpdate(sql9);
    }

    /**
     * Metoda dodająca produkt do koszyka.
     *
     * @param productId - nazwa produktu
     * @param number    - liczba produktu
     */
    public void addProduct(String productId, int number) throws SQLException, ClassNotFoundException {
        String productName = onlineShop.getProductName(productId);
        Products products = new Products(connection, productName);

        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();
        boolean inDict = false;
        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                String bo = changeProductValueMore(productName, number);
                System.out.println("\nProdukt " + productName + " znajduje się juz w twoim koszyku. Zmodyfikowano jego ilość.");
                if (!bo.equals("false")) {
                    inDict = true;
                }
            }
        }
        if (!inDict) {
            if (products.getAvailability() >= number) {
                cartProducts.put(products, number);
                System.out.println("\nPomyślnie dodano produkt " + productName + " do koszyka.");
            } else {
                System.out.println("\nProdukt nie jest dostępny w podanej ilości (na stanie " + products.getAvailability() + " sztuk).");
            }
        }
    }

    /**
     * Metoda usuwająca liczbę produktów w koszyku.
     *
     * @param productName - nazwa produktu
     * @param toDelete    - liczba produktów do usunięcia
     */
    public String changeProductValueLess(String productName, int toDelete) {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            Integer number = it.getValue();
            int newNumber = number - toDelete;
            if (newNumber > 0) {
                if (productNameMap.getName().equals(productName)) {
                    cartProducts.replace(productNameMap, newNumber);
                } else {
                    System.out.println("\nW twoim koszyku nie ma takiego produktu.");
                }
            } else if (newNumber == 0) {
                deleteOneProduct(productName);
            } else {
                System.out.println("\nW koszyku nie masz takiej ilości produktu.");
            }
        }
        return "done";
    }

    /**
     * Metoda dodająca liczbę produktów w koszyku.
     *
     * @param productName - nazwa produktu
     * @param toAdd       - liczba produktów do dodania
     */
    public String changeProductValueMore(String productName, int toAdd) throws SQLException, ClassNotFoundException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();
        Products products = new Products(connection, productName);
        boolean done = false;

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                Integer number = it.getValue();
                int newNumber = number + toAdd;
                if (products.getAvailability() >= newNumber) {
                    cartProducts.replace(productNameMap, newNumber);
                    done = true;
                } else {
                    System.out.println("\nNa stanie nie ma takiej ilości produktu.");
                }
            }
        }
        if(!done) {
            System.out.println("\nW twoim koszyku nie ma takiego produktu.");
        }
        return "done";
    }

    /**
     * Metoda usuwajaca jedną liczbę produktu.
     *
     * @param productName - produkt którego chcemy usunąć 1 liczbę
     */
    public void deleteOneProduct(String productName) {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                cartProducts.remove(productName);
            }
        }
    }

    public void clearCart() {
        cartProducts.clear();
    }

    public void showCart(int userId) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        if (s.isEmpty()) {
            System.out.println("\nTwój koszyk jest pusty.");
            return;
        }

        int i = 1;
        System.out.println("\nTwój koszyk:");
        for (Map.Entry<Products, Integer> it : s) {
            Products productName = it.getKey();
            Integer number = it.getValue();
            System.out.println("Produkt " + i + ": " + productName.getName() + ", cena za sztukę (zł): " + productName.getPrice() + ", liczba sztuk: " + number);
            i++;
        }
        cartOperations(userId);
    }

    public void cartOperations(int userId) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        Menu menu = new Menu();
        String action = menu.getInput("Co chcesz dalej zrobić? \n1 - kupić produkty z koszyka \n2 - edytować ilość danego produktu \n3 - wyczyścić koszyk \n4 - wrócić do menu");
        switch (action) {
            case "1":
                saveCart(userId);
                buyCart(userId);
                break;
            case "2":
                wantToChangeAmount(userId, menu);
                break;
            case "3":
                String result = menu.getInput("Czy na pewno chcesz wyczyścic koszyk? (tak/nie)");
                while (!"tak".equals(result) && !"nie".equals(result)) {
                    System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                    result = menu.getInput("Czy na pewno chcesz wyczyścic koszyk? (tak/nie)");
                }
                if ("tak".equals(result)) {
                    clearCart();
                    System.out.println("\nTwój koszyk został wyczyszczony.");
                }
                menu.startMenu();
                break;
            case "4":
                menu.startMenu();
                break;
            default:
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                cartOperations(userId);
        }
    }

    private void wantToChangeAmount(int userId, Menu menu) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (cartProducts.isEmpty()) {
            System.out.println("\nTwój koszyk jest pusty. Brak produktów do edycji ilości.");
            return;
        }

        String product = menu.getInput("Którego produktu chcesz zmienić ilość?");
        String moreOrLess = menu.getInput("Chcesz zmiejszyć jego ilość w koszyku czy zwiększyć? \n1 - zmiejszyć \n2 - zwiększyć");
        while (!"1".equals(moreOrLess) && !"2".equals(moreOrLess)) {
            System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
            moreOrLess = menu.getInput("Chcesz zmiejszyć jego ilość w koszyku czy zwiększyć? \n1 - zmiejszyć \n2 - zwiększyć");
        }
        int number = 0;
        while (number == 0) {
            try {
                number = Integer.valueOf(menu.getInput("O ile sztuk? (podaj liczbę)"));
            } catch (IllegalArgumentException e) {
                System.out.println("Błąd. Podaj liczbę.");
            }
        }
        switch (moreOrLess) {
            case "1":
                changeProductValueLess(product, number);
                showCart(userId);
                break;
            case "2":
                changeProductValueMore(product, number);
                showCart(userId);
                break;
        }
    }


    /**
     * Metoda sprawdzająca historię zamówień.
     *
     * @param clientId - ID klienta
     */
    public void checkOrdersHistory(int clientId) throws SQLException {
        int counter = 1;

        PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, status, order_value from orders_history where client_id='" + clientId + "' group by id;", ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet orderDetails = selectAllSt.executeQuery();

        if (orderDetails.next()) {
            System.out.println("\nHISTORIA ZAMÓWIEŃ");
            System.out.println("---------------------------------------------------------------------------------------");

            PreparedStatement selectAllSt2 = connection.prepareStatement("select first_name, last_name from orders_history where id=" + clientId + " limit 1;");
            ResultSet client = selectAllSt2.executeQuery();
            while (client.next()) {
                String clientString = client.getString(1);
                String clientString2 = client.getString(2);

                System.out.printf("| %30s |%n", "IMIĘ I NAZWISKO KLIENTA: " + clientString + " " + clientString2);
            }
            System.out.println("---------------------------------------------------------------------------------------");

            orderDetails.beforeFirst();
            while (orderDetails.next()) {
                int orderId = orderDetails.getInt(1);
                String date = orderDetails.getString(2);
                String status = orderDetails.getString(3);
                String value1 = orderDetails.getString(4);

                System.out.printf("| %10s |%n", "ZAMÓWIENIE NR " + counter);
                System.out.printf("| %8s | %20s | %20s | %26s |%n", "ID", "DATA UTWORZENIA", "STATUS ZAMÓWIENIA", "WARTOŚĆ ZAMÓWIENIA [ZŁ]");
                System.out.printf("| %8s | %20s | %20s | %26s |%n", orderId, date, status, value1);
                System.out.println(" ");
                System.out.printf("| %16s |%n", "ZAMÓWIONE PRODUKTY");

                PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from orders_history where order_id='" + orderId + "';");
                ResultSet orderedItems = selectAllSt1.executeQuery();

                while (orderedItems.next()) {
                    String productsId = orderedItems.getString(1);
                    int quantity = orderedItems.getInt(3);
                    double value = orderedItems.getInt(4);

                    PreparedStatement selectAllSt5 = connection.prepareStatement("select product_name from orders_history where product_id='" + productsId + "';");
                    ResultSet name = selectAllSt5.executeQuery();
                    while (name.next()) {
                        String nameString = name.getString(1);
                        System.out.printf("| %30s | %8s | %10s | %10s |%n", "PRODUKT", "ID PRODUKTU", "LICZBA", "WARTOŚĆ ZAMÓWIENIA [ZŁ]");
                        System.out.printf("| %30s | %11s | %10s | %23s |%n", nameString, productsId, quantity, value);
                        System.out.println("\n---------------------------------------------------------------------------------------");
                    }
                }
                counter += 1;
            }
        } else {
            System.out.println("\nBrak zamówień w historii.");
        }
    }

    public void wantToSaveCart(int userId) throws SQLException, ClassNotFoundException {
        Menu menu = new Menu();
        String action = menu.getInput("Czy chcesz zostawić zakupy w koszyku do następnego logowania? \n1 - tak \n2 - nie");
        switch (action) {
            case "1":
                saveCart(userId);
                break;
            case "2":
                break;
            default:
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                wantToSaveCart(userId);
        }
    }

    /**
     * Metoda zapisująca koszyk.
     *
     * @param clientId - ID klienta
     */
    public double saveCart(int clientId) throws SQLException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        if (s.isEmpty()) {
            System.out.println("\nTwój koszyk jest pusty.");
            return 0;
        }
        Statement stmt = connection.createStatement();

        PreparedStatement selectAllSt2 = connection.prepareStatement("select id from carts order by id desc limit 1");
        ResultSet cartToDelete = selectAllSt2.executeQuery();

        while (cartToDelete.next()) {
            int cartToDeleteInt = cartToDelete.getInt(1);

            String sql00 = "delete from cart_items where cart_id='" + cartToDeleteInt + "';";
            stmt.executeUpdate(sql00);

            String sql0 = "delete from carts where id='" + cartToDeleteInt + "';";
            stmt.executeUpdate(sql0);
        }
        String sql = "INSERT INTO carts(client_id) VALUES(" + clientId + ");";
        stmt.executeUpdate(sql);

        double cartValue = 0;

        for (Map.Entry<Products, Integer> it : s) {
            Products product = it.getKey();
            Integer number = it.getValue();

            stmt = connection.createStatement();

            String idString = product.getId();
            double priceInt = product.getPrice();

            double itemsValue = number * priceInt;

            PreparedStatement selectAllSt3 = connection.prepareStatement("select id from carts order by id desc  limit 1;");
            ResultSet cartTempId = selectAllSt3.executeQuery();
            cartTempId.next();
            String cartId = cartTempId.getString(1);

            sql = "INSERT INTO cart_items(product_id, cart_id, quantity, item_value) VALUES('" + idString + "'," + cartId + "," + number + "," + itemsValue + ");";
            stmt.executeUpdate(sql);

            cartValue += itemsValue;
        }
        stmt = connection.createStatement();

        sql = "update carts set cart_value=" + cartValue + "where client_id=" + clientId + ";";
        stmt.executeUpdate(sql);

        return cartValue;
    }

    /**
     * Metoda powodująca kupienie zawartości koszyka.
     *
     * @param clientId - ID klienta
     */
    public void buyCart(int clientId) throws SQLException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        if (s.isEmpty()) {
            System.out.println("Dodaj produkty do koszyka, aby móc złożyć zamówienie.");
            return;
        }
        double cartValue = saveCart(clientId);

        Statement stmt = connection.createStatement();

        String sql = "insert into order_details(client_id, created_date, order_value, status)" +
                " values (" + clientId + ", current_timestamp()," + cartValue + ", 'utworzone');";
        stmt.executeUpdate(sql);

        String orderId = "";
        for (Map.Entry<Products, Integer> it : s) {
            Products product = it.getKey();
            Integer number = it.getValue();

            PreparedStatement selectAllSt1 = connection.prepareStatement("select id from product_stats where product_id='" + product.getId() + "';");
            ResultSet rs1 = selectAllSt1.executeQuery();
            if (rs1.next()) {
                int productStatsId = rs1.getInt(1);

                PreparedStatement selectAllSt2 = connection.prepareStatement("select purchased_quantity from product_stats where product_id='" + productStatsId + "';");
                ResultSet rs2 = selectAllSt2.executeQuery();
                if (rs2.next()) {
                    int quantity = rs2.getInt(1);
                    String sql9 = "update product_stats set purchased_quantity=" + quantity + "+" + number + " where id='" + productStatsId + "';";
                    stmt.executeUpdate(sql9);
                } else {
                    int quantity = 0;

                    String sql9 = "update product_stats set purchased_quantity=" + quantity + "+" + number + " where id='" + productStatsId + "';";
                    stmt.executeUpdate(sql9);
                }
            }
            PreparedStatement selectAllSt3 = connection.prepareStatement("select id from order_details order by id desc limit 1;");
            ResultSet orderTempId = selectAllSt3.executeQuery();
            while (orderTempId.next()) {
                orderId = orderTempId.getString(1);

                double itemsValue = number * product.getPrice();

                String sql1 = "INSERT INTO order_items(product_id, order_id, quantity, items_value) VALUES('" + product.getId() + "','" + orderId + "'," + number + "," + itemsValue + ");";
                stmt.executeUpdate(sql1);

                PreparedStatement selectAllSt6 = connection.prepareStatement("select id from carts where client_id='" + clientId + "';");
                ResultSet cartId = selectAllSt6.executeQuery();
                while (cartId.next()) {
                    int cart = cartId.getInt(1);

                    String sql7 = "delete from cart_items where cart_id=" + cart + ";";
                    stmt.executeUpdate(sql7);
                }
                String sql5 = "delete from carts where client_id='" + clientId + "';";
                stmt.executeUpdate(sql5);

                String sql8 = "update products set availability=availability-" + number + " where id='" + product.getId() + "';";
                stmt.executeUpdate(sql8);
            }
        }
        clearCart();
        PreparedStatement select = connection.prepareStatement("select email from clients_info where user_id='" + clientId + "';");
        String email = onlineShop.getResult(select.executeQuery());
        System.out.println("\nPoprawnie złożono zamówienie. \nAby zamówienie zostało zrealizowane prosimy dokonać płatności " + cartValue + " zł na wskazny numer bankowy: 0000111113333344444 o tytule przelewu \"Numer zamówienia " + orderId + "\". \nDziękujemy za zakupy! \nSzczegółowe informacje związane ze złożonym zamówieniem wysłano na adres email: " + email);
    }
}