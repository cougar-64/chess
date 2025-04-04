package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ConnectionManager;
import websocket.commands.*;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private Integer gameID;
    String username;
    UserGameCommand command;
    private DataAccess dataaccess; // THIS MIGHT NEED TO BE ASSIGNED???
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
                case MAKE_MOVE -> makeMove(session, username, (MakeMove) command);
                case LEAVE -> leaveGame(session, username, (Leave) command);
                case RESIGN -> resign(session, username, (Resign) command);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private String getUsername(String authToken) {
        return dataaccess.getAuthData(authToken).username();
    }

    private String isRootClient() {
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
        String playerColor = isRootClient();
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

    public void makeMove(Session session, String username, MakeMove command) {

    }

    public void leaveGame(Session session, String username, Leave command) {

    }

    public void resign(Session session, String username, Resign command) {

    }

    public void saveSession(Integer gameID, Session session) {

    }
}
