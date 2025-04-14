package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import server.ServerFacade;
import exception.ResponseException;
import model.*;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.*;

import java.io.IOException;
import java.util.*;

public class Client implements NotificationHandler {
    private ChessGame game;
    private String url;
    private WebSocketFacade ws;
    private String playerColor;
    private ServerFacade serverFacade;
    private String username;
    private String authToken;
    HashMap<Integer, GameData> gameList = new HashMap<>(); // needed up here to keep a memory of what game was where. Cleared each time list is called
    public Client(String url) {
        this.url = url;
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
            System.out.println("Error: cannot read input. Please enter your username and password with a single " +
                    "space in between and without a comma, like this: " +
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
            System.out.println("please enter a username, password, and email you would like to use, like this: " +
                    "exampleUser examplePassword example@email.com. " +
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
                System.out.println("Error: cannot read input. Please enter a username, password, and email " +
                        "with a single space in between and without a comma, like this: " +
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
                        join(username);
                        break;
                    case "observe":
                        observe();
                        break;
                    case "logout":
                        logout();
                        break;
                    case "quit":
                        quit();
                        return;
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
            System.out.println("Please enter the name for your new game: (note, this will only create the game." +
                    " You must still join the game after). " +
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
            serverFacade.create(authToken, words[0]);
            System.out.println("successfully created");
            list();

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
            for (int i = 0; i < listOfGames.games().size(); i++) {
                gameList.put(i, listOfGames.games().get(i));
            }
            if (gameList.isEmpty()) {
                System.out.println("There are no current games! Please create a game");
            }
            for (Map.Entry<Integer, GameData> entry : gameList.entrySet()) {
                GameData game = entry.getValue();
                System.out.println("Game " + entry.getKey() + ": " + "Game Name: " + game.gameName() + " WHITE: " +
                        game.whiteUsername() + ", BLACK:" + game.blackUsername());
            }
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
    private void join(String username) {
        initGameList();
        if (gameList.isEmpty()) {
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
                var gameNum = Integer.parseInt(words[0]);
                if (gameNum < 0 || gameNum > gameList.size()-1) {
                    System.out.println("Error: entered number is either less than 1 or higher than the amount of games listed. Try again");
                    continue;
                }
            } catch (Exception e) {
                System.err.println("Error: not a number! Please enter a valid number");
                continue;
            }
            try {
                if (words[1].equals("WHITE")) {
                    playerColor = "WHITE";
                }
                else if (words[1].equals("BLACK")) {
                    playerColor = "BLACK";
                }
                GameData gameData = serverFacade.join(authToken, words[0], words[1], gameList);
                game = gameData.game();
                ws = new WebSocketFacade(url, this);
                ws.connect(gameData, authToken);
                joinedGameMenu(username, words[1], ws, gameData);
            } catch (ResponseException e) {
                System.err.println(e.getMessage());
                System.out.println("Please try again");
            }
        }
    }
    private void observe() {
        initGameList();
        String gameNumber;
        Scanner scanner = new Scanner(System.in);
        String[] words;
        while (true) {
            System.out.println("Please enter the game number you wish to observe. " +
                    "This game number corresponds with the list of games. " +
                    "to view the list again, type 'list'. Or type '..' to return to the main menu");
            gameNumber = scanner.nextLine();
            words = gameNumber.split("\\s+");
            if (gameNumber.equals("..")) {
                postLoginMenu(username);
            }
            if (words.length == 1) {
                if (gameNumber.equals("list")) {
                    list();
                    continue;
                }
                try {
                    var gameNum = Integer.parseInt(words[0]);
                    if (gameNum < 0 || gameNum > gameList.size()-1) {
                        System.out.println("Error: entered number is either less than 0 or higher than the amount of games listed. Try again");
                        continue;
                    }
                } catch (Exception e) {
                    System.err.println("Error: not a number! Please enter a valid number");
                    continue;
                }
                break;
            }
            System.out.println("Error: too many words. Please input the game number you want to view," +
                    " or type 'list' to see the list of games, " +
                    "or type '..' to return to the main menu");
        }
        try {
            GameData gameData = gameList.get(Integer.parseInt(words[0]));
            game = gameData.game();
            ws = new WebSocketFacade(url, this);
            ws.connect(gameData, authToken);
            playerColor = "WHITE";
            while (true) {
                System.out.println("Type 'leave' at any point to leave the game");
                String input = scanner.nextLine();
                if (input.equals("leave")) {
                    leave(ws, gameData);
                }
                else if (input.equals("redraw")) {
                    redraw(ws);
                }
                else if (input.equals("highlight")) {
                    highlight(playerColor);
                }
                else if (input.equals("help")) {
                    observeHelp();
                }
                else {
                    System.out.println("Error: could not interpret input");
                }
            }
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

    public void observeHelp() {
        System.out.println("""
                redraw - redraws the board
                leave - leave the game
                highlight - highlight a position on the board
                help - shows this menu again""");
    }
    public void joinedGameMenu(String username, String playerColor, WebSocketFacade ws, GameData game) {
        if (isLoggedIn) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the game, " + username + "! You are currently playing as " + playerColor + ". type 'help' to get started");
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "help":
                        joinedHelp();
                        break;
                    case "redraw":
                        redraw(ws);
                        break;
                    case "leave":
                        leave(ws, game);
                        break;
                    case "move":
                        makeMove(ws, username, playerColor, game);
                        break;
                    case "resign":
                        resign(ws, authToken, game, username, playerColor);
                        break;
                    case "highlight":
                        highlight(playerColor);
                        break;
                    default:
                        System.out.println("Invalid input! Please type 'help' to get started");
                }
            }
        }
    }
    private void joinedHelp() {
        System.out.println("""
                Please type one of the following:
                help - see actions you can take
                redraw - redraws the chessboard with updated information
                leave - leave the game
                move - makes a move
                resign - resign from your current game
                highlight - highlights all legal moves""");
    }
    private void redraw(WebSocketFacade ws) {
        LoadGame game = new LoadGame(this.game);
        String json = new Gson().toJson(game);
        ws.loadGame(json);
    }
    private void leave (WebSocketFacade ws, GameData game) {
        try {
            ws.leave(game, authToken);
            postLoginMenu(username);
        } catch (ResponseException e) {
            System.err.println(e.getMessage());
        }
    }
    private void makeMove(WebSocketFacade ws, String username, String playerColor, GameData game) {
        String startSquare;
        String endSquare;
        String promoPiece;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter the row and column of the piece you would like to move (i.e. 12 for a2)");
            startSquare = scanner.nextLine();
            if (startSquare.length() == 2 && Character.isDigit(startSquare.charAt(0)) && Character.isDigit(startSquare.charAt(1))) {
                break;
            }
            System.out.println("Please enter a move that is 2 characters long! i.e. 12");
        }
        while (true) {
            System.out.println("Enter the row and column you would like to move your piece to (i.e. 13 for a3");
            endSquare = scanner.nextLine();
            if (endSquare.length() == 2 && Character.isDigit(endSquare.charAt(0)) &&
                    Character.isDigit(endSquare.charAt(1))) {
                break;
            }
            System.out.println("Please enter a move that is 2 characters long! i.e. 13");
        }
        while (true) {
            System.out.println("Enter a promotion piece (if applicable) i.e. Q, K, R, B, for Queen, Knight, Rook, or Bishop respectively");
            promoPiece = scanner.nextLine();
            if (promoPiece.isEmpty()) {
                promoPiece = null;
                break;
            }
            else {
                if (promoPiece.length() == 1) {
                    if (promoPiece.equals("Q") || promoPiece.equals("K") || promoPiece.equals("R") || promoPiece.equals("B")) {
                        break;
                    }
                }
            }
            System.out.println("Error: Please enter a valid promotion piece!");
        }
        try {
            ws.makeMove(startSquare, endSquare, promoPiece, game, authToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void resign(WebSocketFacade ws, String authToken, GameData gameData, String username, String playerColor) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Are you sure you want to resign? y/n");
            String input = scanner.nextLine();
            if (input.equals("y")) {
                try {
                    ws.resign(gameData, authToken);
                    postLoginMenu(username);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if (input.equals("n")) {
                joinedGameMenu(username, playerColor, ws, gameData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void highlight(String playerColor) {
        Scanner scanner = new Scanner(System.in);
        String[] rowAndCol;
        while (true) {
            System.out.println("What square do you want to highlight moves for?");
            String square = scanner.nextLine();
            if (square.length() == 2) {
                rowAndCol = new String[] { String.valueOf(square.charAt(0)), String.valueOf(square.charAt(1)) };
                break;
            } else {
                System.out.println("Error: Please enter a valid square (i.e., 12 for a2)");
            }
        }

        ChessPosition position = new ChessPosition(Integer.parseInt(rowAndCol[1]), Integer.parseInt(rowAndCol[0]));
        Collection<ChessMove> validMoves = game.validMoves(position);
        DrawingBoard draw = new DrawingBoard(game.getBoard());
        if (playerColor.equals("BLACK"))
            draw.printBoardFromBlack(validMoves);
        else {
            draw.printBoardFromWhite(validMoves);
        }
    }
    public void notify(Notification notification) {
        String message = notification.getMessage();
        System.out.println(message);
    }
    public void errorify(websocket.messages.Error error) {
        System.out.println(error.getMessage());
    }
    public void loadGamify(LoadGame loadGame) {
        ChessGame game = loadGame.getGame();
        DrawingBoard draw = new DrawingBoard(game.getBoard());
        if (playerColor.equals("WHITE")) {
            draw.printBoardFromWhite(new ArrayList<ChessMove>());
        }
        else {
            draw.printBoardFromBlack(new ArrayList<ChessMove>());
        }
        this.game = game;
    }
}

/*
to do:
-
 */