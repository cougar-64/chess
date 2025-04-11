package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import websocket.messages.Error;


import java.io.IOException;

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
        } catch (IOException e) {
            websocket.messages.Error error = new websocket.messages.Error(e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException io) {
                System.err.println(io.getMessage());
            }
        }
    }

    private String getUsername(String authToken) throws IOException {
        try {
            return dataaccess.getAuthData(authToken).username();
        } catch (Exception e) {
            throw new IOException("Bad auth!");
        }
    }

    private String getPlayerColor() throws IOException {
            GameData game = dataaccess.getGame(gameID);
            if (game == null) {
                throw new IOException("The game you're trying to access does not exist!");
            }
            if (username.equals(game.blackUsername())) {
                return "BLACK";
            } else if (username.equals(game.whiteUsername())) {
                return "WHITE";
            }
            return null;
    }

    private void connect(Session session, String username, Connect command) throws IOException {
        String playerColor;
        connectionManager.addPlayer(gameID, command.getAuthToken(), session);
        try {
            playerColor = getPlayerColor();
        } catch (IOException e) {
            websocket.messages.Error error = new websocket.messages.Error(e.getMessage());
            connectionManager.toClient(command.getAuthToken(), error);
            return;
        }
        if (playerColor != null) {
            var observeMessage = String.format("%s joined the game as %s", username, playerColor);
            Notification notification = new Notification(observeMessage);
            connectionManager.broadcast(gameID, command.getAuthToken(), notification);
        }
        else {
            var observer = String.format("%s joined the game as an observer", username);
            Notification notification = new Notification(observer);
            connectionManager.broadcast(gameID, command.getAuthToken(), notification);
        }
        LoadGame loadGameMessage = new LoadGame(dataaccess.getGame(gameID).game());
        String json = new Gson().toJson(loadGameMessage);
            session.getRemote().sendString(json);
    }

    public void makeMove(Session session, MakeMove command) throws IOException {
        GameData game = dataaccess.getGame(gameID);
        ChessPosition startSquare = command.getStartingSquare();
        int row = startSquare.getRow();
        int col = startSquare.getColumn();
        ChessPosition endSquare = command.getEndingSquare();
        int endRow = endSquare.getRow();
        int endCol = endSquare.getColumn();
        ChessGame.TeamColor teamColor;
        if (getPlayerColor() == "WHITE") {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        if (!game.game().getBoard().getPiece(new ChessPosition(row, col)).getTeamColor().equals(teamColor)) {
            websocket.messages.Error error = new websocket.messages.Error("That square has enemy pieces on it!");
            connectionManager.toClient(command.getAuthToken(), error);
            return;
        }
        try {
            game.game().makeMove(new ChessMove(new ChessPosition(row, col), new ChessPosition(endRow, endCol), command.getPromotionPiece()));
            dataaccess.updateGame(gameID, game.game());
            LoadGame loadGameMessage = new LoadGame(dataaccess.getGame(gameID).game());
            connectionManager.loadGameForAll(gameID, loadGameMessage);
            Notification notification = new Notification("The move made was " + col + ", " + row + "to " + endCol + ", " + endRow);
            connectionManager.broadcast(gameID, command.getAuthToken(), notification);
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
        connectionManager.broadcast(gameID, command.getAuthToken(), notification);
        session.close();
    }

    public void resign(Session session, String username, Resign command) throws IOException {
        if (dataaccess.getGame(gameID).game().isItOver()) {
            websocket.messages.Error error = new websocket.messages.Error("The game is over! Go home (type 'leave')");
            connectionManager.toClient(command.getAuthToken(), error);
            return;
        }
        if (getPlayerColor() == null) {
            websocket.messages.Error error = new websocket.messages.Error("You can't resign as an observer");
            connectionManager.toClient(command.getAuthToken(), error);
            return;
        }
        GameData game = dataaccess.getGame(gameID);
        game.game().setOver();
        dataaccess.updateGameData(getPlayerColor(), game, username);
        var message = String.format("&s resigned", username);
        Notification notification = new Notification(message);
        connectionManager.notifyToAll(gameID, notification);
        session.close();
    }
}
