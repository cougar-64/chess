package dataaccess;
import model.UserData;
import model.AuthData;
import java.util.List;

public interface DataAccess {
    UserData getUser(String username);
    void createUser(UserData r);
    AuthData createAuth(String username);
    AuthData getAuthData(String auth);
    void deleteAuth(AuthData a);
    List listGames();
}
