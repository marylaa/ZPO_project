package org.example;

import java.sql.*;

import static org.example.Cart.printResultSet;

public class Sorting {
    /**
     * Klasa odpowiadająca za metody związane z sortowaniem
     *
     */

    private Connection connection;

    public Sorting(Connection conn) {
        connection = conn;
    }


    /**
     * Metoda drukująca posortowane produkty.
     *
     * @param sortBy - parametr po którym następuje sortowanie
     * @param categoryId - ID kategorii produktów
     * @param direction - kierunek sortowania
     *
     */
    public void getSortedProducts(String sortBy, String direction, String categoryId){


        try {
            Statement stmt = connection.createStatement();
            Connect connect = new Connect();
            connect.makeConnection();
            String sortByPl = " ";

            if(sortBy.equals("rating")){
                sortByPl = "ocena";
            }else if(sortBy.equals("added_date")){
                sortByPl = "data dodania";
            }else if(sortBy.equals("price")){
                sortByPl = "cena";
            }
            System.out.println(sortByPl);

            PreparedStatement selectAllSt = connection.prepareStatement("select name," +  sortBy + " from product_view where category_id='" +categoryId + "' order by " + sortBy + " " + direction + " ;");
            ResultSet rs = selectAllSt.executeQuery();
//            printResultSetSorting(rs, sortByPl);
            printResultSet(rs);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Metoda drukująca dane wynikowe z bazy danych związane z sortowaniem.
     *
     * @param resultSet - zapytanie do bazy danych
     * @param SortByPl - parametr po którym nastepuje sortowanie
     *
     */
    public static void printResultSetSorting(ResultSet resultSet, String SortByPl) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        int counter = 1;
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
        System.out.println("");

    }

}
