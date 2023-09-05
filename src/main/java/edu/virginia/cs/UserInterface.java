package edu.virginia.cs;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Character.isUpperCase;


public class UserInterface {
    private Scanner scanner;
    private database db = new database();
    String studentName;

    public void runInterface(){
        initializeScanner();
        db.connect();
        login();
        MainMenu();
    }

    private void MainMenu(){
        String mmOptions = promptAndRead("Main Menu\nWould you like to:\n*Submit a review for a course (press a)" +
                "\n*See reviews for a course (press b)\n*Log out (press c)");
        if(mmOptions.equals("c")) {
            login();
            MainMenu();
        }
        else if(mmOptions.equals("a")) {
            String courseName = promptAndRead("Enter course name in form of department then number (such as 'CS 3140'): ");
            checkCourseName(courseName);
            String[] courseSplit = courseName.split(" ");
            submitReview(courseSplit[0], courseSplit[1], studentName);
        }
        else if(mmOptions.equals("b")){
            String courseName = promptAndRead("Enter course name in form of department then number (such as 'CS 3140'): ");
            checkCourseName(courseName);
            String[] courseSplit = courseName.split(" ");
            seeReview(courseSplit[0], courseSplit[1]);
        }
        else{
            System.out.println("Invalid option: please enter either a, b, or c");
            MainMenu();
        }
    }
    private void submitReview(String dept, String catalogNum, String studentName){
        if(db.checkReviewForStudent(dept, catalogNum, studentName)) {
            System.out.println("This student has already submitted a review for this course. Only one review is allowed per student per course");
            MainMenu();
        } else {
            String message = promptAndRead("Please enter your review message: ");
            String rating = promptAndRead("Enter a numerical rating from 1 - 5 : ");
            while(!db.getRatingInt(rating)){
                rating = promptAndRead("Invalid rating. Enter a numerical rating from 1 - 5 : ");
            }
            db.insertReview(studentName, dept, catalogNum, message, rating);
            System.out.println("Review submitted!");
            MainMenu();
        }
    }
    private void seeReview(String dept, String catalogNum) {
        if(!db.checkCourseForReviews(dept, catalogNum)){
            System.out.println("Entered course does not have any reviews.");
            MainMenu();
        }else{
            ArrayList<String> reviewList = db.getReviews(dept, catalogNum);
            int size = reviewList.size();
            String ratingAvg = reviewList.get(size-1);
            for(int i = 0; i < size-1; i++) {
                System.out.println("    " + (i+1) + ") " + reviewList.get(i));
            }
            System.out.println("    Course average: " + ratingAvg + "/5");
            MainMenu();
        }

    }
    private void login(){
        String login = promptAndRead("Welcome: Login as existing user (press 1) or create new user (press 2)");
        if(login.equals("1")) {
            String username = promptAndRead("Please enter username and password \nusername: ");
            String password = promptAndRead("password: ");
            if(db.checkForStudent(username, password)){
                System.out.println("Logged in!");
                studentName = username;
            } else {
                System.out.println("User information not found: create new user or double check entered information");
                login();
            }
        }
        else if(login.equals("2")) {
            String username = promptAndRead("Please create a username and password \nusername: ");
            String password = promptAndRead("password: ");
            String confirmPass = promptAndRead("confirm password: ");
            if(password.equals(confirmPass)) {
                db.insertStudent(username, password);
                System.out.println("New user created!");
                studentName = username;
            }
            else {
                System.out.println("Passwords do not match- please log in again");
                login();
            }
        } else {
            System.out.println("Invalid entry: please enter either 1 or 2");
            login();
        }
    }
    private void checkCourseName(String course) {
        String[] courseSplit = course.split(" ");
        String dept = courseSplit[0].toUpperCase();
        String catalog = courseSplit[1];
        if(dept.length() > 4 || catalog.length() != 4) {
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
