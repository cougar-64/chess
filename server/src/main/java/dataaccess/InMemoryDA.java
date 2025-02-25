package dataaccess;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Random;

import chess.ChessGame;
import model.*;

public class InMemoryDA implements DataAccess {
    private final HashMap<String, UserData> user = new HashMap<>(); // takes username paired with UserData
    private final HashMap<String, AuthData> auth = new HashMap<>(); // takes authToken paired with AuthData
    private final HashMap<String, GameData> games = new HashMap<>(); // takes gameName paired with GameData

    public UserData getUser(String username) {
        return user.get(username);
    }

    public void createUser(UserData r) {
        user.put(r.username(), r);
    }

    public AuthData createAuth(String username) {
        String authToken = generateAuthToken();
        AuthData a = new AuthData(username, authToken);
        auth.put(username, a);
        return a;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData getAuthData(String authentication) {
        for (HashMap.Entry<String, AuthData> entry : auth.entrySet()) {
            if (entry.getValue().authToken().equals(authentication)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void deleteAuth(AuthData a) {
        for (Iterator<HashMap.Entry<String, AuthData>> iterator = auth.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, AuthData> entry = iterator.next();
            if (entry.getValue().equals(a)) {
                iterator.remove();
            }
        }
    }

    public ArrayList<GameData> listGames() {
        ArrayList<GameData> allGames = new ArrayList<>();
        for (HashMap.Entry<String, GameData> entry : games.entrySet()) {
            allGames.add(entry.getValue());
        }
        return allGames;
    }

    public GameData createGame(String gameName) {
        int gameID = createRandomInt();
        ChessGame game = new ChessGame();
        GameData g = new GameData(gameID, null, null, gameName, game);
        games.put(gameName, g);
        return g;
    }

    public GameData getGame(int gameID) {
        for (HashMap.Entry<String, GameData> game : games.entrySet()) {
            if (game.getValue().gameID() == gameID) {
                return game.getValue();
            }
        }
        return null;
    }

    public void deleteGameDuringUpdate(int gameID) {
        for (Iterator<HashMap.Entry<String, GameData>> iterator = games.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, GameData> entry = iterator.next();
            if (entry.getValue().gameID() == (gameID)) {
                iterator.remove();
            }
        }
    }

    public void updateGameData(String playerColor, GameData game) {
        GameData newGame;
        if (playerColor.equals("WHITE")) {
            newGame = new GameData(game.gameID(), playerColor, game.blackUsername(), game.gameName(), game.game());
        }
        else {
            newGame = new GameData(game.gameID(), game.whiteUsername(), playerColor, game.gameName(), game.game());
        }
        deleteGameDuringUpdate(game.gameID());
        games.put(game.gameName(), newGame);
    }

    public void deleteUserDataBase() {
        user.clear();
    }

    public void deleteAuthDataBase() {
        auth.clear();
    }

    public void deleteGameDataBase() {
        games.clear();
    }

    public int createRandomInt() {
        Random random = new Random();
        int randInt;
        do {
            randInt = 1000 + random.nextInt(9000);
            boolean exists = false;
            for (GameData game : games.values()) {
                if (game.gameID() == randInt) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                break;
            }
        } while (true);

        return randInt;
    }

}
