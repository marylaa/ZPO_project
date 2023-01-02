package org.example;

import java.sql.*;

public class DatabaseContext {
    private Connection connection;

    public DatabaseContext(Connection conn) {
        connection = conn;
    }

    public ResultSet getUser(String userLogin, String userPassword) {
        PreparedStatement functionUserSt = null;
        try {
            String statement = "select user_type from users where login like '" + userLogin + "' and password like '" + userPassword + "';";
            functionUserSt = connection.prepareStatement(statement);
            ResultSet resultFunctionUserSt = functionUserSt.executeQuery();
            return (resultFunctionUserSt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAllCategories() {
        PreparedStatement functionCategoriesSt = null;
        try {
            functionCategoriesSt = connection.prepareStatement("select name from categories order by name asc;");
            ResultSet resultFunctionCategoriesSt = functionCategoriesSt.executeQuery();
            return (resultFunctionCategoriesSt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
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
}
