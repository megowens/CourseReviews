package edu.virginia.cs;

import java.util.Scanner;

import static java.lang.Character.isUpperCase;


public class UserInterface {
    private Scanner scanner;
    private database db = new database();

    public void runInterface(){
        initializeScanner();
        db.connect();
        login();
        MainMenu();
    }
    private void MainMenu(){
        String mmOptions = promptAndRead("Main Menu\nWould you like to:\n*Submit a review for a course (press a)" +
                "\n*See reviews for a course (press b)\n*Log out (press c)");
        if(mmOptions.contains("c")) {
            login();
            MainMenu();
        }
        else if(mmOptions.contains("a")) {
            String courseName = promptAndRead("Enter course name in form of department then number (such as 'CS 3140'): ");
            checkCourseName(courseName);
            String[] courseSplit = courseName.split(" ");
            submitReview(courseSplit[0], courseSplit[1]);
        }
        else if(mmOptions.contains("b")){
            String courseName = promptAndRead("Enter course name in form of department then number (such as 'CS 3140'): ");
            checkCourseName(courseName);
            String[] courseSplit = courseName.split(" ");
            seeReview(courseSplit[0], courseSplit[1]);
        }
    }
    private void submitReview(String dept, String catalogNum){
        //submit a review
    }
    private void seeReview(String dept, String catalogNum) {
        //display a review
    }
    private void login(){
        String login = promptAndRead("Welcome: Login as existing user (press 1) or create new user (press 2)");
        if(login.contains("1")) {
            String username = promptAndRead("Please enter username and password \nusername: ");
            String password = promptAndRead("password: ");
            if(db.checkForStudent(username, password)){
                System.out.println("Logged in!");
            } else {
                System.out.println("User information not found: create new user or double check entered information");
                login();
            }
        }
        else if(login.contains("2")) {
            String username = promptAndRead("Please create a username and password \nusername: ");
            String password = promptAndRead("password: ");
            String confirmPass = promptAndRead("confirm password: ");
            if(password.equals(confirmPass)) {
                db.insertStudent(username, password);
                System.out.println("New user created!");
            }
            else {
                System.out.println("Passwords do not match- please log in again");
                login();
            }
        }
    }
    private void checkCourseName(String course) {
        String[] courseSplit = course.split(" ");
        String dept = courseSplit[0];
        String catalog = courseSplit[1];
        boolean uppercase = true;
        for(int i = 0; i < dept.length(); i++) {
            if(!isUpperCase(dept.charAt(i))){
                uppercase = false;
            }
        }
        if(dept.length() > 4 || !uppercase || catalog.length() > 4) {
            System.out.println("Incorrect format of class name. Enter department initials in capital letters followed by a space and then the catalog number, as in- CS 3140");
            MainMenu();
        }
        try{
            Integer.parseInt(catalog);
        }catch(NumberFormatException n) {
            System.out.println("Incorrect format of class name. Enter department initials followed by a space and then the numeric catalog number, as in- CS 3140");
            MainMenu();
        }
    }
    private void initializeScanner(){
        scanner = new Scanner(System.in);
    }

    private String promptAndRead(String s){
        System.out.println(s);
        return scanner.nextLine();
    }
}
