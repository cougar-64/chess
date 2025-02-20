package dataaccess;
import java.util.HashMap;

import Model.UserData;

public class InMemoryDA implements DataAccess {
    private final HashMap<String, UserData> user = new HashMap<>();

    public UserData getUser(String username) {
        return user.get(username);
    }

    public void createUser(UserData r) {
        user.put(r.username(), r);
    }


}
