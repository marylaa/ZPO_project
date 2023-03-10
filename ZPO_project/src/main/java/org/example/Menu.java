package org.example;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private static String userType;
    private static int userId;
    private static DatabaseContext onlineShop;
    private static Connection connection;
    private static Seller seller;
    private static Buyer buyer;
    private static Cart cart;

    /**
     * Bezparametrowy konstruktor.
     */
    public Menu(){
    }

    /**
     * Metoda symulująca działanie aplikacji.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public void startMenu() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (userId == 0) {
            //https://www.asciiart.eu/food-and-drinks/coffee-and-tea
            System.out.println("                          (   ) )\n             ;,'           ) ( (\n     _o_    ;:;'      .-'---------| \n ,-.'---`.__ ;       ( C|/\\/\\/\\/\\/|\n((j`=====',-'         '-./\\/\\/\\/\\/|\n `-\\     /              '_________'\n    `-=-'                '-------'");
            System.out.println("Witamy w naszym sklepie internetowym");
            login();
        }

        if ("buyer".equals(userType)) {
            String action = getInput("Co chcesz zrobić? \n1 - wyświetlić listę kategorii \n2 - sprawdzić historię zamówień \n3 - sprawdzić zawartość koszyka \n4 - wylogować");
            while (!"1".equals(action) && !"2".equals(action) && !"3".equals(action) && !"4".equals(action)) {
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                action = getInput("Podaj numer");
            }
            switch (action) {
                case "1":
                    buyer.printCategories();
                    break;
                case "2":
                    cart.checkOrdersHistory(userId);
                    startMenu();
                    break;
                case "3":
                    cart.showCart(userId);
                    startMenu();
                    break;
                case "4":
                    cart.wantToSaveCart(userId);
                    logout(cart);
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
                    logout(null);
                    break;
            }
        }
    }

    /**
     * Metoda pobierająca dane od użytkownika.
     *
     * @param prompt zapytanie
     * @return String pobrane dane
     */
    public String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n" + prompt);
        return scanner.nextLine();
    }

    /**
     * Metoda logujaca używkowika do aplikacji.
     *
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws ClassNotFoundException
     */
    public void login() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        this.onlineShop = new DatabaseContext(Connect.makeConnection("user_login"));
        String userLogin = getInput("Podaj login");
        String userPassword = getInput("Podaj hasło");
        String[] data = onlineShop.getUserInfo(userLogin);
        while (data[0] == null) {
            System.out.println("\nNie znaleziono użytkownika o podanym loginie. Spróbuj ponownie.");
            userLogin = getInput("Podaj login");
            userPassword = getInput("Podaj hasło");
            data = onlineShop.getUserInfo(userLogin);
        }
        String hashedPassword = hashPassword(userPassword, data[2]);
        if (data[1].equals(hashedPassword)) {
            this.userId = Integer.valueOf(data[0]);
            this.userType = String.valueOf(data[3]);
            System.out.println("\nPoprawnie zalogowano.");

            if ("buyer".equals(userType)) {
                this.connection = Connect.makeConnection("buyer");
                this.onlineShop = new DatabaseContext(connection);
                this.cart = new Cart(userId, onlineShop, connection);
                this.buyer = new Buyer(cart, onlineShop, connection);
            } else {
                this.connection = Connect.makeConnection("seller");
                this.onlineShop = new DatabaseContext(connection);
                this.seller = new Seller(onlineShop, connection);
            }
        } else {
            System.out.println("\nLogowanie nie powiodło się. Spróbuj ponownie.");
            login();
        }
    }

    /**
     * Metoda szyfrująca hasło.
     * https://www.baeldung.com/java-password-hashing
     *
     * @param password hasło do zaszyfrowania
     * @param salt     sól szyfrująca
     * @return String zaszyfrowane hasło
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);
        SecretKey key = skf.generateSecret(spec);
        byte[] hashedBytes = key.getEncoded();
        String hashedString = Hex.encodeHexString(hashedBytes);
        return hashedString;
    }

    /**
     * Metoda wylogowujaca używkowika z aplikacji.
     *
     * @param cart koszyk użytkownika
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public void logout(Cart cart) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String action = getInput("Czy na pewno chcesz się wylogować? (tak/nie)");
        switch (action) {
            case "tak":
                this.userId = 0;
                this.userType = null;
                if (cart != null) {
                    cart.clearCart();
                }
                startMenu();
                break;
            case "nie":
                startMenu();
                break;
            default:
                System.out.println("Nierozpoznana akcja. Spróbuj ponownie.");
                logout(cart);
        }
    }

    /**
     * Metoda zwracająca id użytkownika.
     *
     * @return int id użytkownika
     */
    public int getId() {
        return userId;
    }
}
