package Service;
import dataaccess.DataAccess;
import server.Registration_info;

import javax.xml.crypto.Data;

public class Service {
    private final DataAccess dataaccess;
    public Service(DataAccess dataaccess) {
        this.dataaccess = dataaccess;
    }

    public String register(Registration_info req) {
        var info = dataaccess.getUser(req.username());
        if (info == null) {

        }
    }
}
