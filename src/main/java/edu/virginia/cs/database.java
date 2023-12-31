package edu.virginia.cs;

import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;


public class database {
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
            Rating INTEGER not NULL CHECK (1 <= Rating <= 5),
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
    public void insertReview(String studentName, String dept, String catalogNum, String message, String rating) {
        try {
            int studentID = getStudentID(studentName);
            int courseID = getCourseID(dept, catalogNum, true);
            int ratingInt = Integer.parseInt(rating);
            String sql = "INSERT INTO Reviews(StudentID, CourseID, Message, Rating) VALUES(?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, studentID);
            pstmt.setInt(2, courseID);
            pstmt.setString(3, message);
            pstmt.setInt(4, ratingInt);
            pstmt.executeUpdate();
        }catch(SQLException e) {
            throw new IllegalArgumentException("error: Unable to insert review into table");
        }
    }
    public void insertCourse(String department, String catalogNum){
        try {
            String sql = "INSERT INTO Courses(Department, Catalog_Number) VALUES(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, department);
            pstmt.setString(2, catalogNum);
            pstmt.executeUpdate();
        }catch(SQLException e) {
            throw new IllegalArgumentException("error: Unable to insert course into table");
        }
    }

    protected boolean getRatingInt(String rating) {
        try {
            int ratingInt = Integer.parseInt(rating);
            if(ratingInt < 1 || ratingInt > 5) {
                System.out.println("Rating not a valid integer. Enter a integer from 1 - 5");
                return false;
            }
        }catch(NumberFormatException n) {
            System.out.println("Rating not a valid integer. Enter a integer from 1 - 5");
            return false;
        }
        return true;
    }

    /**returns true if student has already submitted a review - that is if the reviews table has a row
     * with the students ID number and the course ID number*/
    public boolean checkReviewForStudent(String dept, String catalogNum, String studentName){
        try {
            int studentID = getStudentID(studentName);

            int courseID = getCourseID(dept, catalogNum, true);

            String query = "Select (count(*) > 0) as found FROM Reviews where StudentID = ? AND CourseID = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, studentID);
            pst.setInt(2, courseID);
            ResultSet rsRev = pst.executeQuery();
            if (rsRev.next()) {
                boolean found = rsRev.getBoolean(1);
                return found;
            } else {
                return false;
            }
        }catch(SQLException e) {
            System.out.println("error: unable to check reviews table for student's previous reviews");
        }
        return false;
    }

    /**
     *returns true is the course has at least one review, return false is course has no reviews
     */
    public boolean checkCourseForReviews(String dept, String catalog) {
        try{
            int courseID = getCourseID(dept, catalog, false);
            String query = "Select (count(*) > 0) as found FROM Reviews where courseID = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, courseID);
            ResultSet rsRev = pst.executeQuery();
            if (rsRev.next()) {
                boolean found = rsRev.getBoolean(1);
                return found;
            } else {
                return false;
            }
        }catch(SQLException e) {
            System.out.println("error: unable to check reviews table for course's previous reviews");
        }
        return false;
    }

    public ArrayList<String> getReviews(String dept, String catalog){
        try {
            ArrayList<String> reviewsList = new ArrayList<>();
            int courseID = getCourseID(dept, catalog, false);
            String getRevQuery = "Select * from Reviews where CourseID = ?";
            PreparedStatement pst = connection.prepareStatement(getRevQuery);
            pst.setInt(1, courseID);
            ResultSet rs = pst.executeQuery();
            int totalRatings = 0;
            int total = 0;
            while (rs.next()) {
                String message = rs.getString("Message");
                reviewsList.add(message);
                totalRatings += rs.getInt("Rating");
                total++;
            }
            int avgRating = totalRatings / total;
            String ratingAvg = Integer.toString(avgRating);
            reviewsList.add(ratingAvg);
            return reviewsList;
        } catch(SQLException e) {
            throw new IllegalStateException("error: could not retrieve reviews from table");
        }
    }

    /**
     * have exception as insert call because in the user interface all course titles are checked for validity
     * before this method is ever called, therefore any course that gets sent through here is valid to add
     * to the table. The nested statement is for if there really is an issue with the table, though it should
     * be caught by other methods before ever getting caught here
     */
    private int getCourseID(String dept, String catalogNum, boolean insertIfAbsent) {
        try {
            String courseInfoQuery = "Select * from Courses where Department = ? AND Catalog_Number = ?";
            PreparedStatement CoursePst = connection.prepareStatement(courseInfoQuery);
            CoursePst.setString(1, dept);
            CoursePst.setString(2, catalogNum);
            ResultSet courseRS = CoursePst.executeQuery();
            int courseID = courseRS.getInt("id");
            return courseID;
        }catch(SQLException e){
            if(insertIfAbsent) {
                insertCourse(dept, catalogNum);
                int courseID = getCourseID(dept, catalogNum, insertIfAbsent);
                return courseID;
            }else{
                int courseID = 0;
                return courseID;
            }
        }
    }
    private int getStudentID(String studentName) {
        try {
            String studentInfo = "Select * from Students where Name = ?";
            PreparedStatement studentPST = connection.prepareStatement(studentInfo);
            studentPST.setString(1, studentName);
            ResultSet rs = studentPST.executeQuery();
            int studentID = rs.getInt("id");
            return studentID;
        }catch(SQLException e) {
            System.out.println("current user not in student database, please restart the app");
            System.exit(0);
        }
        return 0;
    }
}
