package edu.virginia.cs;

public class Main {
    public static void main(String[] args) {
        database Database = new database();
        UserInterface user = new UserInterface();

        Database.setUpDBandTables();

        user.runInterface();
    }
}
