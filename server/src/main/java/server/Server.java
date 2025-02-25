package server;

import model.*;
import dataaccess.InMemoryDA;
import spark.*;
import com.google.gson.Gson;
import java.util.Map;
import service.Service;
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
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::deleteDataBase);
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
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    private Object register(Request req, Response res) throws ResponseException {
        var info = new Gson().fromJson(req.body(), UserData.class);
        Service s = new Service(da);
        var registered = s.registerRequest(info);
        if (registered == null) {
            throw new ResponseException(403, "Error: already taken");
        }
        res.status(200);
        return new Gson().toJson(registered);
    }


    private Object login(Request req, Response res) throws ResponseException {
            var info = new Gson().fromJson(req.body(), UserData.class);
            Service s = new Service(da);
            var loggedIn = s.loginRequest(info);
            res.status(200);
            return new Gson().toJson(loggedIn);

    }


    private Object logout(Request req, Response res) throws ResponseException {
            String authToken = req.headers("authorization");
            Service s = new Service(da);
            s.logoutRequest(authToken);
            res.status(200);
            return "{}";
    }

    private String getGame(Request req, Response res) throws ResponseException {
            String authToken = req.headers("authorization");
            Service s = new Service(da);
            var result = s.gameListRequest(authToken);
            res.status(200);
            return new Gson().toJson(result);
    }

    private Object createGame(Request req, Response res) throws ResponseException {
            String authToken = req.headers("authorization");
            var info = new Gson().fromJson(req.body(), GameData.class);
            Service s = new Service(da);
            var result = s.createGameRequest(authToken, info);
            res.status(200);
            return new Gson().toJson(result);

    }

    private Object joinGame(Request req, Response res) throws ResponseException{
            String authToken = req.headers("authorization");
            var info = new Gson().fromJson(req.body(), Map.class);
            String playerColor = (String) info.get("playerColor");
            if (info.get("gameID") == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            double gameIDDouble = (double) info.get("gameID");
            int gameID = (int) gameIDDouble;
            Service s = new Service(da);
            s.joinGameRequest(authToken, playerColor, gameID);
            res.status(200);
            return "{}";
    }

    private Object deleteDataBase(Request req, Response res) {
        Service s = new Service(da);
        s.deleteDataBase();
        res.status(200);
        return "{}";
    }

}
