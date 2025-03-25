package ui;
import server.ServerFacade;
import exception.ResponseException;
import model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {
    private ServerFacade serverFacade;
    private String username;
    private String authToken;
    HashMap<Integer, GameData> gameList = new HashMap<>(); // needed up here to keep a memory of what game was where. Cleared each time list is called
    public Client(String url) {
        this.serverFacade = new ServerFacade(url);
    }
    boolean isLoggedIn = false;

    public void preLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to CS 240 chess! ♛ ♚ ♞ \n Type 'help' to get started");
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "help":
                    preHelp();
                    break;
                case "quit":
                    quit();
                    break;
                case "register":
                    preRegister();
                    break;
                case "login":
                    preLogin();
                    break;
                case "clear":
                    clear();
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

    public void quit() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Are you sure you want to quit? y/n ");
            String input = scanner.next();
            if (input.equals("y")) {
                System.exit(0);
            }
            else if (input.equals("n")) {
                preLoginMenu();
            }
        }
    }

    private void preLogin() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        System.out.println("Please enter your username and password like this: exampleUser examplePassword. Or type '..' to return to the main menu");
        while (true) {
            String loginCredentials = scanner.nextLine();
            words = loginCredentials.split("\\s+");
            if (words.length == 2) {
                break;
            }
            else if (loginCredentials.equals("..")) {
                preLoginMenu();
            }
            System.out.println("Error: cannot read input. Please enter your username and password with a single space in between and without a comma, like this: " +
                    "exampleUser examplePassword, or type '..' to return to the main menu");
        }
        try {
            AuthData auth = serverFacade.login(words[0], words[1]);
            isLoggedIn = true;
            username = auth.username();
            authToken = auth.authToken();
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
        postLoginMenu(username);
    }

    private void preRegister() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] words;
            System.out.println("please enter a username, password, and email you would like to use, like this: exampleUser examplePassword example@email.com. " +
                    "Or type '..' to go back to the main menu");
            while (true) {
                String registerCredentials = scanner.nextLine();
                words = registerCredentials.split("\\s+");
                if (words.length == 3) {
                    break;
                }
                else if (registerCredentials.equals("..")) {
                    preLoginMenu();
                }
                System.out.println("Error: cannot read input. Please enter a username, password, and email with a single space in between and without a comma, like this: " +
                        "exampleUser examplePassword example@email.com, or type '..' to return to the main menu");
            }
            try {
                AuthData auth = serverFacade.register(words[0], words[1], words[2]);
                isLoggedIn = true;
                username = auth.username();
                authToken = auth.authToken();
                postLoginMenu(username);
            } catch (ResponseException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void postLoginMenu(String username) {
        Scanner scanner = new Scanner(System.in);
        if (isLoggedIn) {
            System.out.println("welcome, " + username + "! Type 'help' to see your logged-in menu options");
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "create":
                        create();
                        break;
                    case "list":
                        list();
                        break;
                    case "join":
                        join();
                        break;
                    case "observe":
                        observe();
                        break;
                    case "logout":
                        logout();
                        break;
                    case "quit":
                        quit();
                        break;
                    case "help":
                        postHelp();
                        break;
                    case "clear":
                        clear();
                        break;
                    default:
                        System.out.println("Invalid input! please type 'help' to get started with your logged-in menu");
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

    private GameData create() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the name for your new game: (note, this will only create the game. You must still join the game after). " +
                    "Or type '..' to return to the main menu");
            String gameName = scanner.nextLine();
            words = gameName.split("\\s+");
            if (words.length == 1) {
                break;
            }
            else if (gameName.equals("..")) {
                postLoginMenu(username);
            }
            System.out.println("Error: too many words. Please enter a game name that is a single word, without any spaces, " +
                    "or type '..' to return to the main menu");
        }
        try {
            GameData game = serverFacade.create(authToken, words[0]);
            System.out.println("gameID: " + game.gameID());
            // this function is now finished, it will return to the post-login menu
            postLoginMenu(username);
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void list() {
        gameList.clear();
        try {
            ListGamesResult listOfGames = serverFacade.listGames(authToken);
            for (int i = 1; i < listOfGames.games().size(); i++) {
                gameList.put(i, listOfGames.games().get(i));
            }
            for (Map.Entry<Integer, GameData> entry : gameList.entrySet()){
                GameData game = entry.getValue();
                System.out.println("Game " + entry.getKey() + ": " + "Game ID: " + game.gameID() + " Players: " +
                        game.whiteUsername() + ", " + game.blackUsername());
            }
            // this function is done so it now returns to post-login menu
            postLoginMenu(username);
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
    }

    private void initGameList() {
        try {
            ListGamesResult listOfGames = serverFacade.listGames(authToken);
            for (int i = 1; i < listOfGames.games().size(); i++) {
                gameList.put(i, listOfGames.games().get(i));
            }
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
    }

    private void join() {
        initGameList();
        if (gameList.size() == 0) {
            System.out.println("There are no current games to join");
            System.out.println("Automatically redirecting to create a game...");
            create();
        }
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the game number for the game you want to join (corresponds to the game list), " +
                    "and either WHITE or BLACK to choose the player color (an available color). Or type '..' to return to the main menu");
            String input = scanner.nextLine();
            words = input.split("\\s+");
            if (input.equals("..")) {
                postLoginMenu(username);
                return;
            }
            else if (words.length != 2) {
                System.out.println("Error: too many or too little words. Please enter the number (in the 'list' function) " +
                        "for the game you want to join and then WHITE or BLACK without any commas, or type '..' to return to the main menu");
                continue;
            }
            try {
                serverFacade.join(authToken, words[0], words[1], gameList);
                DrawingBoard draw = new DrawingBoard();
                if (words[1].equals("WHITE")) {
                    draw.printBoardFromWhite();
                } else if (words[1].equals("BLACK")) {
                    draw.printBoardFromBlack();
                }
                // this function is done so it defaults back to the post-login menu
                postLoginMenu(username);
            } catch (ResponseException e) {
                System.err.println(e.getMessage());
                System.out.println("Please try again");
            }
        }
    }

    private void observe() {
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the game number you wish to observe. This game number corresponds with the list of games. " +
                    "to view the list again, type 'list'. Or type '..' to return to the main menu");
            String gameNumber = scanner.nextLine();
            words = gameNumber.split("\\s+");
            if (words.length == 1) {
                if (gameNumber.equals("list")) {
                    list();
                    continue;
                }
                break;
            }
            else if (gameNumber.equals("..")) {
                postLoginMenu(username);
            }
            System.out.println("Error: too many words. Please input the game number you want to view, or type 'list' to see the list of games, " +
                    "or type '..' to return to the main menu");
        }
        // make the call to observe the game - make sure it's from white's view!!
            // find the game by the game number - similar to join. Except instead of joining, just print the game.game() and make sure it's from white's view
        try {
            GameData game = gameList.get(words[0]);
            System.out.print(game.game());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void logout() {
        try {
            serverFacade.logout(authToken);
            isLoggedIn = false;
            System.out.println("Successfully logged out");
            preLoginMenu();
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
    }

    private void clear() {
        // clears the database - meant only for testing purposes
        // is not listed in "help"
        // delete when deployed!!!!
        try {
            serverFacade.clear();
        } catch (ResponseException r) {
            System.err.println(r.getMessage());
        }
    }
}
