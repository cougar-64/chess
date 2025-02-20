package dataaccess;
import java.util.HashMap;
import java.util.UUID;

import Model.UserData;
import Model.AuthData;

public class InMemoryDA implements DataAccess {
    private final HashMap<String, UserData> user = new HashMap<>();
    private final HashMap<String, AuthData> auth = new HashMap<>();

    public UserData getUser(String username) {
        return user.get(username);
    }

    public void createUser(UserData r) {
        user.put(r.username(), r);
    }

    public void createAuth(String username) {
        String authToken = generateAuthToken();
        AuthData a = new AuthData(username, authToken);
        auth.put(username, a);
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
