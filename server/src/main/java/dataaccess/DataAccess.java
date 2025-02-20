package dataaccess;
import Model.UserData;

public interface DataAccess {
    UserData getUser(String username);
    void createUser(UserData r);
    void createAuth(String username);
}
