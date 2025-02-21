package dataaccess;
import model.UserData;
import model.AuthData;

public interface DataAccess {
    UserData getUser(String username);
    void createUser(UserData r);
    String createAuth(String username);
    AuthData getAuth(String auth);
}
