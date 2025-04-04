package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> sessionMap = new ConcurrentHashMap<>(); // integer = gameID, String = authToken, Session = session

    public void addPlayer(int gameID, String authToken, Session session) {
        sessionMap.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        sessionMap.get(gameID).put(authToken, session);
    }

    public void removePlayer(int gameID, String authToken) {
        ConcurrentHashMap<String, Session> innerMap = sessionMap.get(gameID);
        if (innerMap != null) {
            innerMap.remove(authToken);
        }
    }

    public void broadcast(String excludePlayer, Notification notification) throws IOException {
        for (var gameSessions : sessionMap.values()) {
            for (var playerSessionEntry : gameSessions.entrySet()) {
                String authToken = playerSessionEntry.getKey();
                Session session = playerSessionEntry.getValue();
                if (session.isOpen() && !authToken.equals(excludePlayer)) {
                    session.getRemote().sendString(new Gson().toJson(notification));
                }
            }
        }
    }
}
