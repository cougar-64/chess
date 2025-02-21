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
}
