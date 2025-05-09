import chess.*;
import ui.Client;
import ui.websocket.NotificationHandler;
import websocket.messages.Notification;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverURL = "http://localhost:8080";
        new Client(serverURL).preLoginMenu();
    }
}