package org.example;

import java.sql.*;

public class DatabaseContext {
    private Connection connection;

    public DatabaseContext(Connection conn) {
        connection = conn;
    }

    public ResultSet getUser(String userLogin, String userPassword) {
        try {
            String statement = "select user_type from users where login like '" + userLogin + "' and password like '" + userPassword + "';";
            PreparedStatement functionUserSt = connection.prepareStatement(statement);
            return functionUserSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllCategories() {
        PreparedStatement functionCategoriesSt = null;
        try {
            functionCategoriesSt = connection.prepareStatement("select name from categories order by name asc;");
            return functionCategoriesSt.executeQuery();
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
            PreparedStatement functionProductsSt = connection.prepareStatement("select name from products where category_id like '" + categoryId + "';");
            return functionProductsSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCategoryId(String categoryName) throws SQLException {
        PreparedStatement functionCategorySt = connection.prepareStatement("select id from categories where name like '" + categoryName + "';");
        ResultSet resultFunctionCategorySt = functionCategorySt.executeQuery();
        return getResult(resultFunctionCategorySt);
    }

    public ResultSet getProduct() {
        Methods methods = new Methods();

        try {
            String productName = methods.chooseProduct();
            String productId = getProductId(productName);
            while (productId == null) {
                System.out.println("Niepoprawna nazwa produktu. Spróbuj ponownie.");
                productName = methods.chooseProduct();
                productId = getProductId(productName);
            }
            PreparedStatement functionProductSt = connection.prepareStatement("select name, producer, description, price, user_id, availability from products where id like '" + productId + "';");
            return functionProductSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProductId(String productName) throws SQLException {
        PreparedStatement functionProductSt = connection.prepareStatement("select id from products where name like '" + productName + "';");
        ResultSet resultFunctionProductSt = functionProductSt.executeQuery();
        return getResult(resultFunctionProductSt);
    }

    public static void printResultSet(ResultSet resultSet, String description) throws SQLException {
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

    public static void printProductDescription(ResultSet resultSet, String description) throws SQLException {
        System.out.println(description);
        ResultSetMetaData rsmd = resultSet.getMetaData(); // metadane o zapytaniu
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) { // wartosci w rzedach
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print("\n");
                String columnValue = resultSet.getString(i);
                System.out.print(rsmd.getColumnLabel(i) + " - " + columnValue);
            }
            System.out.println("");
        }
    }
}
