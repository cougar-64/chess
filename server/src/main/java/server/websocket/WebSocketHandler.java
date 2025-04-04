package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.serialization.Serializer;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;

public class WebSocketHandler {
    private DataAccess dataaccess;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (Connect) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMove) command);
                case LEAVE -> leaveGame(session, username, (Leave) command);
                case RESIGN -> resign(session, username, (Resign) command);
            }
        } catch (Exception e) {
            
        }
    }

    private String getUsername(String authToken) {
        return dataaccess.getAuthData(authToken).username();
    }

    private void connect(Session session, String username, Connect command) {

    }
}
