package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import ui.DrawingBoard;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    UserGameCommand command;
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;System.out.println("Connecting to WebSocket at: " + socketURI);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case LOAD_GAME -> loadGame(message);
                        case NOTIFICATION -> sendNotification(message);
                        case ERROR -> sendError(message);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(GameData gameData, String authToken) throws ResponseException {
        try {
            command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameData.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(GameData gameData, String authToken) throws ResponseException {
        try {
            command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameData.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void loadGame(String message) {
        LoadGame loadGameMessage = new Gson().fromJson(message, LoadGame.class);
        notificationHandler.loadGamify(loadGameMessage);
    }

    public void sendNotification(String message) {
        Notification notification = new Gson().fromJson(message, Notification.class);
        notificationHandler.notify(notification);
    }

    public void sendError(String message) {
        websocket.messages.Error error = new Gson().fromJson(message, websocket.messages.Error.class);
        notificationHandler.errorify(error);
    }

    public void resign(GameData gameData, String authToken) {
        try {
            command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameData.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void makeMove(String startSquare, String endSquare, String promoPiece, GameData gameData, String authToken) {
        ChessPiece.PieceType promotionPiece;
        if (promoPiece == null) {
            promotionPiece = null;
        }
        else {
            switch (promoPiece) {
                case "Q":
                    promotionPiece = ChessPiece.PieceType.QUEEN;
                    break;
                case "R":
                    promotionPiece = ChessPiece.PieceType.ROOK;
                    break;
                case "K":
                    promotionPiece = ChessPiece.PieceType.KNIGHT;
                    break;
                case "B":
                    promotionPiece = ChessPiece.PieceType.BISHOP;
                    break;
                default:
                    promotionPiece = null;
                    break;
            }
        }
        char[] start = startSquare.toCharArray();
        char[] end = endSquare.toCharArray();
        int startRow = Integer.parseInt(String.valueOf(start[1]));
        int startCol = Integer.parseInt(String.valueOf(start[0]));
        int endRow = Integer.parseInt(String.valueOf(end[1]));
        int endCol = Integer.parseInt(String.valueOf(end[0]));
        try {
            MakeMove makeMove = new MakeMove(authToken, gameData.gameID(), new ChessMove(new ChessPosition(startRow, startCol),
                    new ChessPosition(endRow, endCol), promotionPiece));
            command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameData.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMove));
        } catch (IOException e) {
            sendError(e.getMessage());
        }

    }
}