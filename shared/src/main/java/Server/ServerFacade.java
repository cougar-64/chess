package Server;
import com.google.gson.Gson;
import exception.ResponseException;

import java.io.*;
import java.net.*;
import model.*;

import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        UserData user = new UserData(username, password, email);
        var path = "/user";
        return makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws ResponseException {
        UserData user = new UserData(username, password, null);
        var path = "/session";
        return makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logout(String AuthToken)throws ResponseException {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", AuthToken);
        var path = "/session";
        makeRequest("DELETE", path, null, null, map);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, Map<String, String> headers) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    http.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }
}
