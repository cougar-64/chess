package ui;
import java.util.Scanner;

public class Client {
    boolean isLoggedIn = false;

    public void preLogin() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to CS 240 chess! ♛ ♚ ♞ \n Type 'help' to get started");
            String input = scanner.nextLine();
            switch (input) {
                case "help":
                    help();
                case "quit":
                    System.out.print("Are you sure you want to quit? y/n");
                    input = scanner.next();
                    if (input.equals("y")) {
                        System.exit(0);
                    }
                case "register":
                    register();
                case "login":
                    login();
                    break;
                default:
                    System.out.println("Invalid input! Please type 'help' to get started");
            }
        }
    }

    private void help() {
        System.out.println("""
                Please type one of the following!
                register -- to create an account
                login -- to login to an existing account
                quit -- to quit playing chess and exit the program
                help -- lists possible commands""");
    }

    private void login() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        System.out.println("Please enter your username and password like this: exampleUser examplePassword");
        while (true) {
            while (true) {
                String loginCredentials = scanner.nextLine();
                words = loginCredentials.split("\\s+");
                if (words.length == 2) {
                    break;
                }
                System.out.println("Error: cannot read input. Please enter your username and password with a single space in between and without a comma, like this: exampleUser examplePassword");
            }
            // make a call to the login api endpoint using words[0], words[1] and return anything if needed (phase 2)
        }
    }

    private void register() {

    }

    private void btsLogin(String username, String password) {

    }
}
