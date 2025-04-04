package ui.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import server.ConnectionManager;
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
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);

                    switch (notification.getServerMessageType()) {
                        case LOAD_GAME -> loadGame(message);
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
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void loadGame(String message) {
        LoadGame loadGameMessage = new Gson().fromJson(message, LoadGame.class);
        ChessGame game = loadGameMessage.getGame();
        String playerColor = loadGameMessage.getPlayerColor();
        DrawingBoard draw = new DrawingBoard(game.getBoard());
        if (playerColor.equals("BLACK")) {
            draw.printBoardFromBlack();
        }
        else {
            draw.printBoardFromWhite();
        }
    }
}