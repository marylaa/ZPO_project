package org.example;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Menu {
    private static String userType;
    private static int userId;
    private static DatabaseContext onlineShop;
    private static Seller seller;
    private static Buyer buyer;
    private static Cart cart;

    public Menu() {
        this.onlineShop = new DatabaseContext(Connect.makeConnection());
    }

    public void startMenu() {
        if (userId == 0) {
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
                    cart.checkOrdersHistory(userId);
                    break;
                case "3":
                    //KOSZYK
                    break;
                case "4":
                    //pytanie czy zapisac koszyk
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

        String userLogin = getInput("Podaj login");
        String userPassword = getInput("Podaj hasło");
        String[] data = onlineShop.getUserIdAndPasswordAndSalt(userLogin);
        while (data[0] == null) {
            System.out.println("\nNie znaleziono użytkownika o podanym loginie. Spróbuj ponownie.");
            userLogin = getInput("Podaj login");
            userPassword = getInput("Podaj hasło");
            data = onlineShop.getUserIdAndPasswordAndSalt(userLogin);
        }

        String hashedPassword = hashPassword(userPassword, data[2]);
        if (data[1].equals(hashedPassword)) {
            this.userId = Integer.valueOf(data[0]);
            this.userType = onlineShop.getUserType(userId);
            System.out.println("\nPoprawnie zalogowano.");
        } else {
            System.out.println("\nLogowanie nie powiodło się. Spróbuj ponownie.");
            login();
        }
    }

    public static String hashPassword(String password, String salt) {
        //https://medium.com/@kasunpdh/how-to-store-passwords-securely-with-pbkdf2-204487f14e84
        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] hashedBytes = key.getEncoded();
            String hashedString = Hex.encodeHexString(hashedBytes);
            return hashedString;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
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
}
