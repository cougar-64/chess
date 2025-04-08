package server.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ConnectionManager;
import server.Server;
import websocket.commands.*;
import websocket.messages.*;


import java.io.IOException;
import java.util.Collection;

@WebSocket
public class WebSocketHandler {
    private Integer gameID;
    String username;
    UserGameCommand command;
    private DataAccess dataaccess;
    private ConnectionManager connectionManager = new ConnectionManager();
    public WebSocketHandler(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            Gson gson = new Gson();
            command = gson.fromJson(message, UserGameCommand.class);
            username = getUsername(command.getAuthToken());
            gameID = command.getGameID();

            connectionManager.addPlayer(command.getGameID(), command.getAuthToken(), session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gson.fromJson(message, Connect.class));
                case MAKE_MOVE -> makeMove(session, gson.fromJson(message, MakeMove.class));
                case LEAVE -> leaveGame(session, username, gson.fromJson(message, Leave.class));
                case RESIGN -> resign(session, username, gson.fromJson(message, Resign.class));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getUsername(String authToken) {
        return dataaccess.getAuthData(authToken).username();
    }

    private String getPlayerColor() {
        GameData game = dataaccess.getGame(gameID);
        if (username.equals(game.blackUsername())) {
            return "BLACK";
        }
        else if (username.equals(game.whiteUsername())) {
            return "WHITE";
        }
        return null;
    }

    private void connect(Session session, String username, Connect command) throws IOException {
        connectionManager.addPlayer(gameID, command.getAuthToken(), session);
        String playerColor = getPlayerColor();
        if (playerColor != null) {
            LoadGame loadGameMessage = new LoadGame(dataaccess.getGame(gameID).game(), playerColor);
            String json = new Gson().toJson(loadGameMessage);
            session.getRemote().sendString(json);
            var observerMessage = String.format("%s joined the game as %s", username, playerColor);
            Notification notification = new Notification(observerMessage);
            connectionManager.broadcast(username, notification);
        }
        else {
            var observer = String.format("%s joined the game as an observer", username);
            Notification notification = new Notification(observer);
            connectionManager.broadcast(username, notification);
        }
    }

    public void makeMove(Session session, MakeMove command) throws IOException {
        GameData game = dataaccess.getGame(gameID);
        String startSquare = command.getStartingSquare();
        char colChar = startSquare.charAt(0);
        int row = Character.getNumericValue(startSquare.charAt(1));
        int col = colChar - 'a' + 1;
        String endSquare = command.getEndingSquare();
        char endColChar = endSquare.charAt(0);
        int endRow = Character.getNumericValue(endSquare.charAt(1));
        int endCol = endColChar - 'a' + 1;
        try {
            game.game().makeMove(new ChessMove(new ChessPosition(row, col), new ChessPosition(endRow, endCol), null));
            dataaccess.updateGame(gameID, game.game());
            LoadGame loadGameMessage = new LoadGame(dataaccess.getGame(gameID).game(), getPlayerColor());
            String json = new Gson().toJson(loadGameMessage);
            session.getRemote().sendString(json);
        } catch (InvalidMoveException e) {
            websocket.messages.Error error = new websocket.messages.Error(e.getMessage());
            connectionManager.toClient(command.getAuthToken(), error);
        }

    }

    public void leaveGame(Session session, String username, Leave command) throws IOException {
        String playerColor = getPlayerColor();
        if (playerColor != null) {
            GameData game = dataaccess.getGame(gameID);
            if (playerColor.equals("WHITE")) {
                dataaccess.updateGameData("WHITE", game, null);
            }
            else {
                dataaccess.updateGameData("BLACK", game, null);
            }
        }
        connectionManager.removePlayer(gameID, command.getAuthToken());
        var message = String.format("%s has left the game", username);
        Notification notification = new Notification(message);
        connectionManager.broadcast(username, notification);
        session.close();
    }

    public void resign(Session session, String username, Resign command) {

    }
}
