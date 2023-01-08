package org.example;

import java.sql.*;

public class DatabaseContext {
    private static Connection connection;

    public DatabaseContext(Connection conn) {
        connection = conn;
    }

    public String[] getUserIdAndPasswordAndSalt(String userLogin) {
        try {
            PreparedStatement function = connection.prepareStatement("select id, password, salt from users where login like '" + userLogin + "';");
            ResultSet result = function.executeQuery();
            return getResultAsTable(result);
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

    public ResultSet getAllProducts(String categoryId) {
        try {
            PreparedStatement function = connection.prepareStatement("select name from products where category_id like '" + categoryId + "';");
            return function.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCategoryId(String categoryName) {
        try {
            PreparedStatement function = connection.prepareStatement("select id from categories where name like '" + categoryName + "';");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getProductInfo(String productId) {
        try {
            PreparedStatement function = connection.prepareStatement("select name, producer, description, price, (select concat(first_name, ' ', last_name) from users where id = (select user_id from products where id like '" + productId + "')), availability from products where id like '" + productId + "';");
            return function.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProductId(String productName) {
        try {
            PreparedStatement function = connection.prepareStatement("select id from products where name like '" + productName + "';");
            ResultSet result = function.executeQuery();
            return getResult(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getSellerProducts(int UserId) {
        try {
            PreparedStatement function = connection.prepareStatement("select name from products where user_id = " + UserId + ";");
            ResultSet result = function.executeQuery();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProduct(String id, String category, String name, String producer, String description, double price, int userId, int availability) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into products (id, category_id, name, producer, description, price, added_date, user_id, availability) values " +
                    "('" + id + "', '" + category + "', '" + name + "', '" + producer + "', '" + description + "', " + price + ", '" + java.time.LocalDate.now() + "', " + userId + ", " + availability + ");");
            System.out.println("\nPomyślnie dodano produkt.");
        } catch (SQLException e) {
            System.out.println("Niepoprawne dane");;
        }
    }

    public void editProduct(String id, String[] info) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("update products set name = '" + info[0] + "', producer = '" + info[1] + "', description = '" + info[2] + "', price = " + info[3] +
                    ", availability = " + info[5] + " where id like '" + id + "';");
            System.out.println("\nPomyślnie edytowano produkt.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    public String[] getResultAsTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String[] columnValue = new String[columnsNumber];
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                columnValue[i - 1] = resultSet.getString(i);
            }
        }
        return columnValue;
    }
}
