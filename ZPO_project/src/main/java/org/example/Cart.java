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
    private int clientId;
    private Map<Products, Integer> cartProducts = new HashMap<Products, Integer>();

    /**
     * Metoda tworząca koszyk.
     *
     * @param clientId - ID klienta
     */
    public Cart(int clientId) throws ClassNotFoundException, SQLException {
        this.connection = Connect.makeConnection();
        this.onlineShop = new DatabaseContext(connection);
        try {
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

                        Products products = new Products(productName);
                        cartProducts.put(products, productQuantity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metoda dodająca produkt do koszyka.
     *
     * @param productName - nazwa produktu
     * @param number      - liczba produktu
     */
    public void addProduct(String productName, int number) throws SQLException, ClassNotFoundException {
        Products products = new Products(productName);

        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();
        boolean inDict = false;
        for (Map.Entry<Products, Integer> it : s) {

            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                String bo = changeProductValueMore(productName, number);

                if (!bo.equals("false")) {
                    inDict = true;
                }
            }
        }
        if (!inDict) {
            if (products.checkAvailability() >= number) {
                cartProducts.put(products, number);
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
            if (newNumber >= 0) {
                if (productNameMap.getName().equals(productName)) {

                    cartProducts.replace(productNameMap, newNumber);
                }
            } else {
                System.out.println("Niewłaściwa liczba");
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
        Products products = new Products(productName);

        for (Map.Entry<Products, Integer> it : s) {
            Products productNameMap = it.getKey();
            if (productNameMap.getName().equals(productName)) {
                Integer number = it.getValue();
                int newNumber = number + toAdd;
                if (products.checkAvailability() >= newNumber) {
                    cartProducts.replace(productNameMap, newNumber);
                } else {
                    return "false";
                }
            }
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
        System.out.println("\nKoszyk został wyczyszczony.");
    }

    public void showCart(int userId) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        if (s.isEmpty()) {
            System.out.println("\nTwój koszyk jest pusty.");
        }

        for (Map.Entry<Products, Integer> it : s) {
            Products productName = it.getKey();
            Integer number = it.getValue();
            System.out.println(productName);
            System.out.println("Produkt: " + productName.getName() + ", liczba: " + number);
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
                System.out.println("Kupiono produkt. COS LADNIEJ");
                break;
            case "2":
                //zmiana ilości w koszyku
                break;
            case "3":
                clearCart();
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


    /**
     * Metoda sprawdzająca historię zamówień.
     *
     * @param clientId - ID klienta
     */
    public void checkOrdersHistory(int clientId) {
        try {
            System.out.println("\nHistoria zamówień klienta:");

            System.out.println("\nImię i nazwisko klienta:");
            PreparedStatement selectAllSt2 = connection.prepareStatement("select concat(first_name, ' ', last_name) from users where id=" + clientId + ";");
            ResultSet client = selectAllSt2.executeQuery();
            onlineShop.printResultSet(client, null);

            PreparedStatement selectAllSt = connection.prepareStatement("select id, created_date, order_value from order_details where client_id='" + clientId + "';", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet orderDetails = selectAllSt.executeQuery();

            if (orderDetails.next()) {

                orderDetails.beforeFirst();
                while (orderDetails.next()) {
                    int orderId = orderDetails.getInt(1);
                    String date = orderDetails.getString(2);
                    String value1 = orderDetails.getString(3);

                    System.out.println("Szczegóły zamówienia:");
                    System.out.println("id zamówienia, data utworzenia, wartość zamówienia [zł]");
                    System.out.println(orderId + " " + date + " " + value1);

                    System.out.println("Zamówione produkty : ");
                    PreparedStatement selectAllSt1 = connection.prepareStatement("select product_id, order_id, quantity, items_value from order_items where order_id='" + orderId + "';");
                    ResultSet orderedItems = selectAllSt1.executeQuery();

                    while (orderedItems.next()) {

                        String productsId = orderedItems.getString(1);
                        int quantity = orderedItems.getInt(3);
                        double value = orderedItems.getInt(4);

                        System.out.println(value + " zł");
                        System.out.println(quantity + " szt.");

                        PreparedStatement selectAllSt5 = connection.prepareStatement("select name from products where id='" + productsId + "';");
                        ResultSet name = selectAllSt5.executeQuery();
                        onlineShop.printResultSet(name, null);
                    }
                }
            } else {
                System.out.println("Brak zamówień");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Brak zamówień");
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
    public double saveCart(int clientId) {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        double cartValue = 0;

        for (Map.Entry<Products, Integer> it : s) {
            Products product = it.getKey();
            String productName = product.getName();
            Integer number = it.getValue();

            try {
                Statement stmt = connection.createStatement();

                String idString = product.getId();
                double priceInt = product.getPrice();

                double itemsValue = number * priceInt;

                PreparedStatement selectAllSt3 = connection.prepareStatement("select id from carts order by id desc  limit 1;");
                ResultSet cartTempId = selectAllSt3.executeQuery();
                cartTempId.next();
                String cartId = cartTempId.getString(1);

                String sql = "INSERT INTO cart_items(product_id, cart_id, quantity, item_value) VALUES('" + idString + "'," + cartId + "," + number + "," + itemsValue + ");";
                stmt.executeUpdate(sql);

                cartValue += itemsValue;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Statement stmt = connection.createStatement();

            String sql = "update carts set cart_value=" + cartValue + "where client_id=" + clientId + ";";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cartValue;
    }

    /**
     * Metoda powodująca kupienie zawartości koszyka.
     *
     * @param clientId - ID klienta
     */
    public void buyCart(int clientId) throws ClassNotFoundException {
        Set<Map.Entry<Products, Integer>> s = cartProducts.entrySet();

        double cartValue = saveCart(clientId);

        try {
            Statement stmt = connection.createStatement();
            InProductStats stats = new InProductStats();

            String sql = "insert into order_details(client_id, created_date, order_value, status)" +
                    " values (" + clientId + ", current_timestamp()," + cartValue + ", 'created');";
            stmt.executeUpdate(sql);

            for (Map.Entry<Products, Integer> it : s) {
                Products product = it.getKey();
                Integer number = it.getValue();

                stats.addToProductStats(product.getId());

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
                    String orderId = orderTempId.getString(1);

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}