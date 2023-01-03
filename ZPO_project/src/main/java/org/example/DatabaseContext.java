package org.example;

import java.sql.*;

public class DatabaseContext {
    private Connection connection;

    public DatabaseContext(Connection conn) {
        connection = conn;
    }

    public int getUser() {
        Methods methods = new Methods();

        String[] loginPassword = methods.logIn();
        String id = getUserId(loginPassword[0], loginPassword[1]);
        while (id == null) {
            System.out.println("\nLogowanie nie powiodło się. Spróbuj ponownie.");
            loginPassword = methods.logIn();
            id = getUserId(loginPassword[0], loginPassword[1]);
        }
        return Integer.valueOf(id);
    }

    public String getUserId(String userLogin, String userPassword) {
        try {
            PreparedStatement function = connection.prepareStatement("select id from users where login like '" + userLogin + "' and password like '" + userPassword + "';");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserType(int id) {
        try {
            PreparedStatement function = connection.prepareStatement("select user_type from users where id = " + id + ";");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllCategories() {
        try {
            PreparedStatement function = connection.prepareStatement("select name from categories order by name asc;");
            return function.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllProducts() {
        Methods methods = new Methods();

        try {
            String categoryName = methods.chooseCategory();
            String categoryId = getCategoryId(categoryName);
            while (categoryId == null) {
                System.out.println("Niepoprawna nazwa kategorii. Spróbuj ponownie.");
                categoryName = methods.chooseCategory();
                categoryId = getCategoryId(categoryName);
            }
            PreparedStatement function = connection.prepareStatement("select name from products where category_id like '" + categoryId + "';");
            return function.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCategoryId(String categoryName) {
        try {
            PreparedStatement function = connection.prepareStatement("select id from categories where name like '" + categoryName + "';");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getProductInfo() {
        Methods methods = new Methods();

        try {
            String productName = methods.chooseProduct();
            String productId = getProductId(productName);
            while (productId == null) {
                System.out.println("Niepoprawna nazwa produktu. Spróbuj ponownie.");
                productName = methods.chooseProduct();
                productId = getProductId(productName);
            }
            PreparedStatement function = connection.prepareStatement("select name, producer, description, price, (select concat(first_name, ' ', last_name) from users where id = (select user_id from products where id like '" + productId + "')), availability from products where id like '" + productId + "';");
            return function.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProductId(String productName) {
        try {
            PreparedStatement function = connection.prepareStatement("select id from products where name like '" + productName + "';");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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


    public String getResult(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        String columnValue = null;
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                columnValue = resultSet.getString(i);
            }
        }
        return columnValue;
    }

    public void printProductDescription(ResultSet resultSet, String description) throws SQLException {
        System.out.println(description);
        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        String[] column = {"nazwa produktu", "producent", "opis", "cena (w zł)", "sprzedający", "dostępność (w sztukach)"};
        while (resultSet.next()) { // wartosci w rzedach
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print("\n");
                String columnValue = resultSet.getString(i);
                System.out.print(column[i - 1] + " - " + columnValue);
            }
            System.out.println("");
        }
    }

    public void printSellerProducts(int UserId) {
        try {
            PreparedStatement function = connection.prepareStatement("select name from products where user_id = " + UserId + ";");
            ResultSet result = function.executeQuery();
            printResultSet(result, "\nLista twoich produktów:");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
