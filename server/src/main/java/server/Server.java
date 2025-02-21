package server;

import model.UserData;
import dataaccess.InMemoryDA;
import spark.*;
import com.google.gson.Gson;
import java.util.Map;
import Service.Service;
import exception.ResponseException;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::session);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object register(Request req, Response res) throws ResponseException {
        var info = new Gson().fromJson(req.body(), Map.class);
        String username = (String) info.get("username");
        String password = (String) info.get("password");
        String email = (String) info.get("email");
        if (! (username instanceof String) || ! (password instanceof String) || !(email instanceof String)) {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData u = new UserData(username, password, email);
        InMemoryDA da = new InMemoryDA();
        Service s = new Service(da);
        var registered = s.register(u);
        res.status(200);
        return new Gson().toJson(registered);
    }


    private Object login(Request req, Response res) throws ResponseException {
        var info = new Gson().fromJson(req.body(), Map.class);
        String username = (String) info.get("username");
        String password = (String) info.get("password");
        if (! (username instanceof String) || ! (password instanceof String)) {
            throw new ResponseException(400, "Error: bad request");
        }

    }
}
