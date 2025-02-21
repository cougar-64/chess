package model;
import chess.*;

public record GameData(int GameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
