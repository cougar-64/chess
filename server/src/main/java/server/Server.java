package server;

import model.UserData;
import dataaccess.InMemoryDA;
import spark.*;
import com.google.gson.Gson;
import java.util.Map;
import Service.Service;
import exception.ResponseException;

public class Server {
        InMemoryDA da = new InMemoryDA();
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::getGame);
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

    private Object register(Request req, Response res) {
        try {
            var info = new Gson().fromJson(req.body(), Map.class);
            String username = (String) info.get("username");
            String password = (String) info.get("password");
            String email = (String) info.get("email");
            if (!(username instanceof String) || !(password instanceof String) || !(email instanceof String)) {
                throw new ResponseException(400, "Error: bad request");
            }
            UserData u = new UserData(username, password, email);
            Service s = new Service(da);
            var registered = s.registerRequest(u);
            if (registered == null) {
                throw new ResponseException(403, "Error: Already taken");
            }
            res.status(200);
            return new Gson().toJson(registered);
        } catch (ResponseException r) {
            res.status(r.StatusCode());
            return new Gson().toJson(r.getMessage());
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(e.getMessage());
        }
    }


    private Object login(Request req, Response res) {
        try {
            var info = new Gson().fromJson(req.body(), Map.class);
            String username = (String) info.get("username");
            String password = (String) info.get("password");
            UserData u = new UserData(username, password, null);
            Service s = new Service(da);
            var loggedIn = s.loginRequest(u);
            res.status(200);
            return new Gson().toJson(loggedIn);
        } catch (ResponseException r) {
            res.status(r.StatusCode());
            return new Gson().toJson(r.getMessage());
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(e.getMessage());
        }
    }


    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            Service s = new Service(da);
            s.logoutRequest(authToken);
            res.status(200);
            return "{}";
        } catch (ResponseException r) {
            res.status(r.StatusCode());
            return new Gson().toJson(r.getMessage());
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(e.getMessage());
        }
    }

    private Object getGame(Request req, Response res) {
        try {
            var info = new Gson().fromJson(req.body(), Map.class);
            String authToken = (String) info.get("authToken");
            Service s = new Service(da);
            var result = s.gameListRequest(authToken);
            res.status(200);
            return new Gson().toJson(result);
        } catch (ResponseException r) {
            res.status(r.StatusCode());
            return new Gson().toJson(r.getMessage());
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(e.getMessage());
        }
    }
}
