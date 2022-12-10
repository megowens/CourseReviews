package edu.virginia.cs;

import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTests {
    database db = new database();

    @Test
    public void createTest() throws SQLException, ClassNotFoundException {
        db.connect();

        db.deleteTables();

        db.createTables();

        db.disconnect();
        assertEquals(1, 1);
    }
    @Test
    public void insertStudentTest() throws SQLException, ClassNotFoundException {
        db.connect();
        //db.createTables();
        db.insertStudent("name1", "pass1");
        db.disconnect();
    }
    @Test
    public void checkForStudentTest() throws SQLException, ClassNotFoundException {
        db.connect();
        db.deleteTables();
        //db.createTables();
       // db.checkForStudent("name1", "pass1");
        db.disconnect();
        assertEquals(1, 1);
    }
    @Test
    public void insertReviewTest() throws SQLException {
        db.connect();
        //db.deleteTables();
        //db.createTables();
        db.insertStudent("John", "pass3");
        db.insertCourse("COMM", "3140");
        db.insertCourse("ART", "1100");
        db.insertReview("John", "COMM", "3140", "did not like it", "1");
        db.insertReview("John", "ART", "1100", "really liked", "4");
        db.disconnect();
    }
    @Test
    public void checkReviewForStudentTest() throws SQLException {
        db.connect();
        //db.insertStudent("name1", "pass1");
        //db.insertReview(1, 34, "testing", 5);
        db.checkReviewForStudent("cs", "3140", "name1");
        db.disconnect();
    }
}
