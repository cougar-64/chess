package ui;
import Server.ServerFacade;

import java.util.Scanner;

public class Client {
    private String url;
    public Client(String url) {
        this.url = url;
    }
    boolean isLoggedIn = false;
    ServerFacade serverFacade = new ServerFacade(url);

    public void preLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to CS 240 chess! ♛ ♚ ♞ \n Type 'help' to get started");
            String input = scanner.nextLine();
            switch (input) {
                case "help":
                    preHelp();
                case "quit":
                    System.out.print("Are you sure you want to quit? y/n");
                    input = scanner.next();
                    if (input.equals("y")) {
                        System.exit(0);
                    }
                case "register":
                    preRegister();
                    break;
                case "login":
                    preLogin();
                    break;
                default:
                    System.out.println("Invalid input! Please type 'help' to get started");
            }
        }
    }

    private void preHelp() {
        System.out.println("""
                Please type one of the following!
                register -- to create an account
                login -- to login to an existing account
                quit -- to quit playing chess and exit the program
                help -- lists possible commands""");
    }

    private void preLogin() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        System.out.println("Please enter your username and password like this: exampleUser examplePassword");
        while (true) {
            String loginCredentials = scanner.nextLine();
            words = loginCredentials.split("\\s+");
            if (words.length == 2) {
                break;
            }
            System.out.println("Error: cannot read input. Please enter your username and password with a single space in between and without a comma, like this: exampleUser examplePassword");
        }
        // make a call to the login api endpoint using words[0], words[1] and return anything if needed (phase 2)
        isLoggedIn = true;
        postLoginMenu();
    }

    private void preRegister() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
         System.out.println("please enter a username, password, and email you would like to use, like this: exampleUser examplePassword example@email.com");
         while (true) {
             String registerCredentials = scanner.nextLine();
             words = registerCredentials.split("\\s+");
             if (words.length == 3) {
                 break;
             }
             System.out.println("Error: cannot read input. Please enter a username, password, and email with a single space in between and without a comma, like this: exampleUser examplePassword example@email.com");
         }
         // make a call to the register api endpoint using words[0], words[1], and words[2]
        isLoggedIn = true;
         postLoginMenu();
    }

    public void postLoginMenu(String username) {
        Scanner scanner = new Scanner(System.in);
        if (isLoggedIn) {
            while (true) {
                System.out.println("welcome," + username + "! Type 'help' to see your logged-in menu options");
                String input = scanner.nextLine();
                switch (input) {
                    case ("create"):
                        create();
                    case ("list"):
                        list();
                    case ("join"):
                        join();
                    case ("observe"):
                        observe();
                    case ("logout"):
                        logout();
                        break;
                    case ("quit"):
                        System.out.print("Are you sure you want to quit? y/n");
                        input = scanner.next();
                        if (input.equals("y")) {
                            System.exit(0);
                        }
                    case ("help"):
                        postHelp();
                }
            }
        }
    }

    private void postHelp() {
        System.out.print("""
                create - create a new game
                list - lists all current games
                join - join a game (to play)
                observe - observe a game (to watch)
                logout - logout of your account
                quit - quit the program
                help - lists possible commands
                """);
    }

    private void create() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the name for your new game: (note, this will only create the game. You must still join the game after)");
            String gameName = scanner.nextLine();
            words = gameName.split("\\s+");
            if (words.length == 2) {
                break;
            }
            System.out.println("Error: too many words. Please enter a game name that is a single word, without any spaces");
        }
        // make server call to endpoint
    }

    private void list() {
        // make the call to the server and get the list of games back.
    }

    private void join() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the game ID for the game you want to join, as well as either WHITE or BLACK for the player color");
            String input  = scanner.nextLine();
            words = input.split("\\s+");
            if (words.length == 2) {
                break;
            }
            System.out.println("Error: too many or too little words. Please enter the ID for the game you want to join and then WHITE or BLACK without any commas");
        }
        // make the call to join the game
    }

    private void observe() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the game number you wish to observe. This game number corresponds with the list of games. to view the list again, type 'list'");
            String gameNumber = scanner.nextLine();
            words = gameNumber.split("\\s+");
            if (words.length == 1) {
                if (gameNumber.equals("list")) {
                    list();
                    continue;
                }
                break;
            }
            System.out.println("Error: too many words. Please input the game number you want to view, or type 'list' to see the list of games");
        }
        // make the call to observe the game - make sure it's from white's view!!
    }

    private void logout() {
        // make the server call to log out
        isLoggedIn = false;
        preLoginMenu();
    }
}
