package com.example.phonebook;

import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class JDBCquery {
    public static boolean createNewPhonebook(String phonebookName) {
        Connection conn = null;
        Statement statement = null;
        boolean exists = databaseExists(phonebookName);

        String strQuery = "CREATE TABLE $tableName (" +
                "ID INT NOT NULL," +
                "firstName VARCHAR(45) NOT NULL," +
                "lastName VARCHAR(45) NOT NULL," +
                "phoneNumber VARCHAR(16) NOT NULL," +
                "addrStreet VARCHAR(45)," +
                "addrCity VARCHAR(45)," +
                "addrPost VARCHAR(45)" +
                ");";

        String query = strQuery.replace("$tableName",phonebookName);


        if(!exists) {
            try {

                conn = JDBCUtil.getConnection();

                statement = conn.createStatement();


                statement.executeUpdate(query);
                statement.close();
                conn.close();

                System.out.println("Table created sucessfully");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return exists;
    }
    public static ArrayList<String> loadPhonebookList() {
        ArrayList<String> phonebooksList = new ArrayList<String>();

        Connection conn = null;
        Statement statement = null;

        String query = "Show tables";

        try {

            conn = JDBCUtil.getConnection();

            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                phonebooksList.add(rs.getString(1));
            }
            conn.close();
            statement.close();

            System.out.println("Table list loaded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return phonebooksList;
    }
    private static boolean databaseExists(String phonebookName) {

        Connection conn = null;
        Statement statement = null;

        String query = "Show tables";

        boolean exists = false;


        try {
            conn = JDBCUtil.getConnection();

            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                if(phonebookName.equals(rs.getString(1))) {
                    exists = true;
                }
            }
            conn.close();
            statement.close();
            System.out.println("Checked if table exists successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    public static void deletePhonebook(String phonebookName) {
        Connection conn = null;
        Statement statement = null;
        boolean exists = databaseExists(phonebookName);

        String strQuery = "DROP TABLE $tableName";

        String query = strQuery.replace("$tableName",phonebookName);
        if(exists) {
            try {

                conn = JDBCUtil.getConnection();
                statement = conn.createStatement();

                statement.executeUpdate(query);
                statement.close();
                conn.close();

                System.out.println("Table deleted sucessfully");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void addRecord(String tableName, String firstName, String lastName, String phoneNo, String streetAdr, String cityAdr, String postAdr) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        int nextID = getNextID(tableName);

        String strQuery = "INSERT INTO $tableName (ID, firstName, lastName, phoneNumber, addrStreet, addrCity, addrPost)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        String query = strQuery.replace("$tableName", tableName);

        try {

            conn = JDBCUtil.getConnection();
            preparedStatement = conn.prepareStatement(query);

            preparedStatement.setInt(1, nextID);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, phoneNo);
            preparedStatement.setString(5, streetAdr);
            preparedStatement.setString(6, cityAdr);
            preparedStatement.setString(7, postAdr);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();

            System.out.println("Record inserted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static int getNextID(String tableName) {
        int nextID = -1;
        Connection conn = null;
        Statement statement = null;

        String strQuery = "SELECT MAX(ID) AS nextID FROM $tableName";

        String query = strQuery.replace("$tableName", tableName);
        try {
            conn = JDBCUtil.getConnection();
            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                nextID = rs.getInt("nextID") + 1;
                System.out.println(nextID);
            }
            if(nextID < 0) {
                nextID = 1;
            }

            statement.close();
            conn.close();

            System.out.println("Next ID loaded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextID;
    }
    public static boolean recordExists(String tableName, String firstName, String lastName, String phoneNo) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        String strQuery = "SELECT ID FROM $tableName WHERE firstName = ? AND lastName = ? AND phoneNumber = ?";
        String query = strQuery.replace("$tableName", tableName);

        try {
            conn = JDBCUtil.getConnection();
            preparedStatement = conn.prepareStatement(query);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNo);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                exists = true;
            }
            preparedStatement.close();
            conn.close();

            System.out.println("Checked if record exists sucessfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    public static void searchRecords(String currentTable, String searchCriteria, String searchText, ObservableList<ContactInformation> contacts) {
        Connection conn = null;
        Statement statement = null;

        String strQuery = "SELECT * FROM $tableName WHERE $searchCriteria LIKE '$searchText%'";
        String query = strQuery.replace("$tableName", currentTable);
        query = query.replace("$searchCriteria", searchCriteria);
        query = query.replace("$searchText", searchText);

        try {
            conn = JDBCUtil.getConnection();
            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                contacts.add(new ContactInformation(
                        rs.getInt("ID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phoneNumber"),
                        rs.getString("addrStreet"),
                        rs.getString("addrCity"),
                        rs.getString("addrPost")
                ));
            }

            statement.close();
            conn.close();

            System.out.println("Records searched sucessfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteRecord(String currentTable, int id) {
        Connection conn = null;
        Statement statement = null;

        String strQuery = "DELETE FROM $tableName WHERE ID = $contactID";
        String query = strQuery.replace("$tableName", currentTable);
        query = query.replace("$contactID", Integer.toString(id));

        try {
            conn = JDBCUtil.getConnection();
            statement = conn.createStatement();

            statement.executeUpdate(query);

            conn.close();
            statement.close();

            System.out.println("Record deleted sucessfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateRecord(String currentTable, int id, String queryText) {

        Connection conn = null;
        PreparedStatement preparedStatement = null;

        String strQuery = "UPDATE $tableName SET $queryText WHERE ID = ?";
        String query = strQuery.replace("$tableName", currentTable);
        query = query.replace("$queryText", queryText);

        try {
            conn = JDBCUtil.getConnection();
            preparedStatement = conn.prepareStatement(query);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            conn.close();
            preparedStatement.close();

            System.out.println("Record updated sucessfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadGetAll(String currentTable, ObservableList<ContactInformation> contacts) {
        Connection conn = null;
        Statement statement = null;

        String strQuery = "SELECT * FROM $tableName";
        String query = strQuery.replace("$tableName", currentTable);

        try {
            conn = JDBCUtil.getConnection();
            statement = conn.createStatement();

            ResultSet rs = statement.executeQuery(query);
            while(rs.next()) {
                contacts.add(new ContactInformation(
                        rs.getInt("ID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phoneNumber"),
                        rs.getString("addrStreet"),
                        rs.getString("addrCity"),
                        rs.getString("addrPost")
                ));
            }

            statement.close();
            conn.close();

            System.out.println("Records searched sucessfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
