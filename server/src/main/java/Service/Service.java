package Service;
import dataaccess.DataAccess;
import model.UserData;
import exception.ResponseException;
import spark.Response;

public class Service {
    private final DataAccess dataaccess;
    public Service(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }

    public String register(UserData req) throws ResponseException {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            dataaccess.createUser(req);
            return dataaccess.createAuth(req.username());
        }
        else {
            throw new ResponseException(403, "Error: Already taken");
        }
    }
}
