package dataaccess;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.List;

import model.UserData;
import model.AuthData;

public class InMemoryDA implements DataAccess {
    private final HashMap<String, UserData> user = new HashMap<>();
    private final HashMap<String, AuthData> auth = new HashMap<>();

    public UserData getUser(String username) {
        return user.get(username);
    }

    public void createUser(UserData r) {
        user.put(r.username(), r);
    }

    public AuthData createAuth(String username) {
        String authToken = generateAuthToken();
        AuthData a = new AuthData(username, authToken);
        auth.put(username, a);
        return a;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData getAuthData(String authentication) {
        for (HashMap.Entry<String, AuthData> entry : auth.entrySet()) {
            if (entry.getValue().authToken().equals(authentication)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void deleteAuth(AuthData a) {
        for (Iterator<HashMap.Entry<String, AuthData>> iterator = auth.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, AuthData> entry = iterator.next();
            if (entry.getValue().equals(a)) {
                iterator.remove();
            }
        }
    }

    public List listGames() {

    }
}
