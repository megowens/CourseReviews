package edu.virginia.cs;

public class Main {
    public static void main(String[] args) {
        //first step : make database - use separate class and 1 call for that
        database Database = new database();
        UserInterface user = new UserInterface();

        Database.setUpDBandTables();
        user.runInterface();
    }
}
