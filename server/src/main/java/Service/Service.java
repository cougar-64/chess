package Service;
import dataaccess.DataAccess;
import model.UserData;
import model.AuthData;
import exception.ResponseException;
import java.util.List;

public class Service {
    private final DataAccess dataaccess;
    public Service(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }


    public AuthData registerRequest(UserData req) throws ResponseException {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            dataaccess.createUser(req);
            return dataaccess.createAuth(req.username());
        }
        else {
            return null;
        }
    }


    public AuthData loginRequest(UserData req) throws ResponseException {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (info.password().equals(req.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return dataaccess.createAuth(req.username());
    }


    public void logoutRequest(String authToken) throws ResponseException {
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        dataaccess.deleteAuth(auth);
    }


    public List gameListRequest(String authToken) {
        AuthData auth = dataaccess.getAuthData(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        dataaccess.listGames();
    }
}
