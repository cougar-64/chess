package Service;
import dataaccess.DataAccess;
import Model.UserData;

public class Service {
    private final DataAccess dataaccess;
    public Service(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }

    public String register(UserData req) {
        var info = dataaccess.getUser(req.username());
        if (info == null) {
            dataaccess.createUser(req);
            dataaccess.createAuth(req.username());
        }
        else if (info != null) {
            return "Error: already taken";
        }
        return "test";
    }
}
