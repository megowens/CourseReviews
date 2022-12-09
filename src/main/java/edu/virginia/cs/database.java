package edu.virginia.cs;

import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;


public class database {
    //create three tables plus management for them (adding/deleting rows, retrieving info)
    private Connection connection;

    public void setUpDBandTables() {
        try {
            checkDatabase("Reviews.sqlite3");
            connect();
            createTables();
        }catch(SQLException e) {
            throw new RuntimeException("error: tables not created correctly");
        }
    }
    public void checkDatabase(String fileName) {
        String url = "jdbc:sqlite:" + fileName;
        try(Connection conn = DriverManager.getConnection(url)) {
            if(conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is: " + meta.getDriverName());
                //System.out.println("A new database has been created");
            }
        } catch(SQLException e ) {
            System.out.println("error in the creation of database");
        }
    }
    public void connect() {
        try {
            if (connection != null) {
                throw new IllegalStateException("Manager is already connected");
            }
            String databaseName = "Reviews.sqlite3";
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
        }catch(SQLException e ) {
            throw new RuntimeException("error: Unable to connect to database due to SQL issues");
        }catch(ClassNotFoundException e ){
            throw new RuntimeException("error: Unable to connect to database due to class not being found");
        }
    }
    public void disconnect() throws SQLException {
        if(connection == null) {
            throw new IllegalStateException("Manager is already disconnected");
        }
        connection.close();
    }
    public void createTables() throws SQLException {
        try {
            //students table
            String createStudentsTableQuery = """
            CREATE TABLE if not exists Students ( id INTEGER not NULL, 
            Name VARCHAR (250) not NULL UNIQUE, 
            Password VARCHAR(250) not NULL, 
            PRIMARY KEY (id) )""";
            Statement studentStatement = connection.createStatement();
            studentStatement.executeUpdate(createStudentsTableQuery);

            //courses table
            String createCoursesTableQuery = """
            CREATE TABLE if not exists Courses ( id INTEGER not NULL,
            Department VARCHAR(4) not NULL,
            Catalog_Number VARCHAR(4) not NULL,
            PRIMARY KEY (ID) )""";
            Statement CoursesStatement = connection.createStatement();
            CoursesStatement.executeUpdate(createCoursesTableQuery);

            //reviews table
            String createReviewsTableQuery = """
            CREATE TABLE if not exists Reviews (
            id INTEGER not NULL,
            StudentID INTEGER not NULL,
            CourseID INTEGER not NULL,
            Message VARCHAR(500) not NULL,
            Rating INTEGER not NULL CHECK (0 <= Rating <= 5),
            FOREIGN KEY (StudentID) references Students(id) ON DELETE CASCADE,
            FOREIGN KEY (CourseID) references Courses(id) ON DELETE CASCADE,
            PRIMARY KEY (ID) ) """;
            Statement ReviewsStatement = connection.createStatement();
            ReviewsStatement.executeUpdate(createReviewsTableQuery);
        }catch(NullPointerException e) {
            throw new IllegalStateException("Manager not connected");
        }
    }

    public void deleteTables() throws SQLException {
        try {
            String deleteStudentsQuery = "DROP TABLE if exists Students";
            String deleteCoursesQuery = "DROP TABLE if exists Courses";
            String deleteReviewsQuery = "DROP TABLE if exists Reviews";

            Statement deleteStudentsStatement = connection.createStatement();
            Statement deleteCoursesStatement = connection.createStatement();
            Statement deleteReviewsStatement = connection.createStatement();

            deleteStudentsStatement.executeUpdate(deleteStudentsQuery);
            deleteCoursesStatement.executeUpdate(deleteCoursesQuery);
            deleteReviewsStatement.executeUpdate(deleteReviewsQuery);
        }catch(SQLiteException e) {
            throw new IllegalStateException("Tables do not exist");
        }catch(NullPointerException e) {
            throw new IllegalStateException("Manager not connected");
        }
    }
    public void insertStudent(String studentName, String studentPassword) {
        try {
             String sql = "INSERT INTO Students(Name, Password) VALUES(?,?)";
             PreparedStatement pstmt = connection.prepareStatement(sql);
             pstmt.setString(1, studentName);
             pstmt.setString(2, studentPassword);
             pstmt.executeUpdate();
        }catch(SQLException e) {
            throw new IllegalArgumentException("error: Unable to insert student into table");
        }
    }
    public boolean checkForStudent(String name, String password) {
        try {
            String query = "Select (count(*) > 0) as found FROM Students where Name = ? AND Password = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                boolean found = rs.getBoolean(1);
                if(found) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }catch(SQLException e){
            throw new IllegalArgumentException("error: Could not properly check for student");
        }
        return false;
    }
}
