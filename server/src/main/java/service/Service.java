package service;
import dataaccess.DataAccess;
import model.GameData;
import model.UserData;
import model.AuthData;
import exception.ResponseException;
import org.eclipse.jetty.server.Response;

import java.util.ArrayList;

public class Service {
    private final DataAccess dataaccess;
    public Service(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }


    public AuthData registerRequest(UserData req) throws ResponseException {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            dataaccess.createUser(req);
            return dataaccess.createAuth(req.username());
        }
        else {
            return null;
        }
    }


    public AuthData loginRequest(UserData req) throws ResponseException {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (! info.password().equals(req.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return dataaccess.createAuth(req.username());
    }


    public void logoutRequest(String authToken) throws ResponseException {
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        dataaccess.deleteAuth(auth);
    }


    public ArrayList<GameData> gameListRequest(String authToken) throws ResponseException {
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return dataaccess.listGames();
    }

    public int createGameRequest(String authToken, String gameName) throws ResponseException{
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return dataaccess.createGame(gameName);
    }

    public void joinGameRequest(String authToken, String playerColor, int gameID) throws ResponseException {
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        GameData game = dataaccess.getGame(gameID);
        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        String playerColorSuccess = getPlayerColor(playerColor, game);
        dataaccess.updateGameData(playerColorSuccess, game);
    }

    public String getPlayerColor(String playerColor, GameData game) throws ResponseException {
        /**
         * The purpose of this function is to handle the logic of checking if
         * the desired player color is already taken and throw exceptions for
         * already taken colors and input that is not "BLACK/WHITE".
         * Thus, we don't need to worry about checking for anything in the caller method.
         */
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new ResponseException(400, "Error: bad request");
            }
            return "WHITE";
        }

        else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new ResponseException(400, "Error: bad request");
            }
            return "BLACK";
        }

        else {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public void deleteDataBase() {
        dataaccess.deleteUserDataBase();
        dataaccess.deleteAuthDataBase();
        dataaccess.deleteGameDataBase();
    }
}
