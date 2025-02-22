package dataaccess;
import model.*;
import java.util.ArrayList;

public interface DataAccess {
    UserData getUser(String username);
    void createUser(UserData r);
    AuthData createAuth(String username);
    AuthData getAuthData(String auth);
    void deleteAuth(AuthData a);
    ArrayList<GameData> listGames();
    int createGame(String gameName);
    int createRandomInt();
    GameData getGame(int gameID);
    void updateGameData(String playerColor, GameData game);
    void deleteGameDuringUpdate(int gameID);
}
