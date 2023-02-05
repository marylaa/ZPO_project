package org.example;

import java.sql.*;

public class DatabaseContext {
    private Connection connection;

    /**
     * Bezparametrowy konstruktor.
     */
    public DatabaseContext() {
    }

    /**
     * Parametrowy konstruktor.
     *
     * @param connection obiekt klasy Connection
     */
    public DatabaseContext(Connection connection) {
        this.connection = connection;
    }

    /**
     * Metoda zwracająca informacje o użytkowniku.
     *
     * @param userLogin login użytkownika
     * @return tablica informacji
     * @throws SQLException
     */
    public String[] getUserInfo(String userLogin) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select id, password, salt, user_type from login_view where login like '" + userLogin + "';");
        ResultSet result = function.executeQuery();
        return getResultAsTable(result);
    }

    /**
     * Metoda zwracająca wszystkie dostępne kategorie.
     *
     * @return ResultSet nazwy kategorii
     * @throws SQLException
     */
    public ResultSet getAllCategories() throws SQLException {
        PreparedStatement function = connection.prepareStatement("select distinct category_name from product_view order by category_name asc;");
        return function.executeQuery();
    }

    /**
     * Metoda zwracająca wszystkie produkty z danej kategorii.
     *
     * @param categoryId id kategorii
     * @return ResultSet nazwy produktów
     * @throws SQLException
     */
    public ResultSet getAllProducts(String categoryId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select name from product_view where category_id like '" + categoryId + "';");
        return function.executeQuery();
    }

    /**
     * Metoda zwracająca id kategorii.
     *
     * @param categoryName nazwa kategorii
     * @return String id kategorii
     * @throws SQLException
     */
    public String getCategoryId(String categoryName) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select category_id from product_view where category_name like '" + categoryName + "';");
        ResultSet result = function.executeQuery();
        return getResult(result);
    }

    /**
     * Metoda zwracająca nazwę kategorii.
     *
     * @param categoryId id kategorii
     * @return String id kategorii
     * @throws SQLException
     */
    public String getCategoryName(String categoryId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select category_name from product_view where category_id like '" + categoryId + "';");
        ResultSet result = function.executeQuery();
        return getResult(result);
    }

    /**
     * Metoda zwracająca informacje o danym produkcie.
     *
     * @param productId id produktu
     * @return ResultSet informacje o produkcie
     * @throws SQLException
     */
    public ResultSet getProductInfo(String productId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select name, producer, description, price, seller, availability from product_view where id like '" + productId + "';");
        return function.executeQuery();
    }

    /**
     * Metoda zwracająca id produktu.
     *
     * @param productName nazwa produktu
     * @return String id produktu
     * @throws SQLException
     */
    public String getProductId(String productName) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select id from product_view where name like '" + productName + "';");
        ResultSet result = function.executeQuery();
        return getResult(result);
    }

    /**
     * Metoda zwracająca nazwę produktu.
     *
     * @param productId id produktu
     * @return String nazwa produktu
     * @throws SQLException
     */
    public String getProductName(String productId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select name from product_view where id like '" + productId + "';");
        ResultSet result = function.executeQuery();
        return getResult(result);
    }

    /**
     * Metoda zwracająca produkty danego sprzedawcy.
     *
     * @param UserId id sprzedawcy
     * @return ResultSet nazwy produktów
     * @throws SQLException
     */
    public ResultSet getSellerProducts(int UserId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select name from seller_product_view where user_id = " + UserId + ";");
        return function.executeQuery();
    }

    /**
     * Metoda dodająca produkt do bazy danych.
     *
     * @param id           id produktu
     * @param category     kategoria produktu
     * @param name         nazwa produktu
     * @param producer     producent
     * @param description  opis produktu
     * @param price        cena produktu
     * @param userId       id sprzedawcy
     * @param availability dostępność produktu
     * @throws SQLException
     */
    public void addProduct(String id, String category, String name, String producer, String description, double price, int userId, int availability) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into products (id, category_id, name, producer, description, price, added_date, user_id, availability) values " +
                "('" + id + "', '" + category + "', '" + name + "', '" + producer + "', '" + description + "', " + price + ", '" + java.time.LocalDate.now() + "', " + userId + ", " + availability + ");");
        System.out.println("\nPomyślnie dodano produkt.");
    }

    /**
     * Metoda edytująca informacje o produkcie.
     *
     * @param id   id produktu
     * @param info tablica informacji
     * @throws SQLException
     */
    public void editProduct(String id, String[] info) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("update products set name = '" + info[0] + "', producer = '" + info[1] + "', description = '" + info[2] + "', price = " + info[3] +
                ", availability = " + info[5] + " where id like '" + id + "';");
        System.out.println("\nPomyślnie edytowano produkt.");
    }

    /**
     * Metoda zwracająca ilość sztuk sprzedanego produktu.
     *
     * @param productId id produktu
     * @return String ilość sztuk
     * @throws SQLException
     */
    public String checkPurchasedQuantityOfProduct(String productId) throws SQLException {
        PreparedStatement function = connection.prepareStatement("select purchased_quantity from product_view where id = '" + productId + "';");
        return getResult(function.executeQuery());
    }

    /**
     * Metoda drukująca posortowane produkty.
     *
     * @param sortBy     - parametr, po którym następuje sortowanie
     * @param categoryId - id kategorii produktów
     * @param direction  - kierunek sortowania
     */
    public void getSortedProducts(String sortBy, String direction, String categoryId) throws SQLException {
        String sortByPl = " ";
        if (sortBy.equals("rating")) {
            sortByPl = "ocena";
        } else if (sortBy.equals("added_date")) {
            sortByPl = "data dodania";
        } else if (sortBy.equals("price")) {
            sortByPl = "cena";
        }
        PreparedStatement function = connection.prepareStatement("select name," + sortBy + " from product_view where category_id = '" + categoryId + "' order by " + sortBy + " " + direction + " ;");
        printResultSetSorting(function.executeQuery(), sortByPl);
    }

    /**
     * Metoda wyświetlająca posortowane produkty.
     *
     * @param resultSet posortowane produkty
     * @param SortByPl  opis, po czym były sortowane
     * @throws SQLException
     */
    public static void printResultSetSorting(ResultSet resultSet, String SortByPl) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        int counter = 1;
        System.out.println("\nPosotowane produkty:");
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", " + SortByPl + " ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue);
                counter += 1;
            }
            System.out.println("");
        }
    }

    /**
     * Metoda zwracająca wynik w postaci Stringa.
     *
     * @param resultSet wynik zapytania
     * @return String
     * @throws SQLException
     */
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

    /**
     * Metoda zwracająca wynik w postacie tablicy String[].
     *
     * @param resultSet wynik zapytania
     * @return String[]
     * @throws SQLException
     */
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

    /**
     * Metoda wyświetlająca wynik zapytania.
     *
     * @param resultSet   wynik zapytania
     * @param description opis
     * @throws SQLException
     */
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
}
