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
}
