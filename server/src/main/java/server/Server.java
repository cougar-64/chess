package server;

import Model.UserData;
import spark.*;
import com.google.gson.Gson;
import java.util.Map;
import Service.Service;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws Exception {
        var info = new Gson().fromJson(req.body(), Map.class);
        String username = null;
        String password = null;
        String email = null;
        try {
            username = (String) info.get("username");
            password = (String) info.get("password");
            email = (String) info.get("email");
        } catch (Exception e) {
            System.out.println("username, password, or email has incorrect syntax!");
        }
        UserData u = new UserData(username, password, email);
        Service s = new Service();
        var registered = s.register(u);
        return new Gson().toJson(registered);
    }
}
