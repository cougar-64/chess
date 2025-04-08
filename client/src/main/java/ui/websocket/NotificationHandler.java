package ui.websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(Notification notification);
    void errorify(websocket.messages.Error error);
}