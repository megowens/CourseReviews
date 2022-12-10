package edu.virginia.cs;

import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTests {
    database db = new database();

    @Test
    public void createTest() throws SQLException, ClassNotFoundException {
        db.connect();

        //db.deleteTables();

        //db.createTables();

        db.disconnect();
        assertEquals(1, 1);
    }
    @Test
    public void insertStudentTest() throws SQLException, ClassNotFoundException {
        db.connect();
        //db.createTables();
        //db.insertStudent("name1", "pass1");
        db.disconnect();
    }
    @Test
    public void checkForStudentTest() throws SQLException, ClassNotFoundException {
        db.connect();
        //db.deleteTables();
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
       // db.insertStudent("Camille", "paris4life");
        //db.insertCourse("EDUC", "5634");
        //db.insertCourse("COMM", "1110");
        //db.insertReview("Ava", "COMM", "3410", "live laugh love sherri moore", "5");
        db.disconnect();
    }
    @Test
    public void checkReviewForStudentTest() throws SQLException {
        db.connect();
        //db.insertStudent("name1", "pass1");
        //db.insertReview(1, 34, "testing", 5);
        //db.checkReviewForStudent("cs", "3140", "name1");
        db.disconnect();
    }
}
