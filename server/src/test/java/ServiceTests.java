import dataaccess.DataAccess;
import dataaccess.InMemoryDA;
import exception.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.Service;
import model.*;
import spark.Response;

import javax.xml.crypto.Data;
import java.sql.Array;
import java.util.ArrayList;

public class ServiceTests {
    private static UserData user;
    private static AuthData auth;
    private static GameData game1;
    private static GameData game2;
    private static GameData game3;
    private static ListGamesResult listGames;
    private static DataAccess dataaccess;
    private static Service serve;
    private static String wrongAuth;

    @BeforeAll
    public static void init() {
        user = new UserData("testUsername", "testPassword", "test@test.com");
        dataaccess = new InMemoryDA();
        serve = new Service(dataaccess);
        auth = new AuthData("testUsername", "testToken");
        game1 = new GameData(1234, null, null, "testName", null);
        game2 = new GameData(1111, null, null, "game2", null);
        game3 = new GameData(4432, null, null, "game3", null);
        ArrayList<GameData> gamedata = new ArrayList<>();
        gamedata.add(game1);
        gamedata.add(game2);
        gamedata.add(game3);
        listGames = new ListGamesResult(gamedata);
        wrongAuth = "wrongAuthToken";
    }

    @Test
    @DisplayName("Successful Register")
    public void RegisterSuccess() throws ResponseException {
        AuthData result = serve.registerRequest(user);
        Assertions.assertEquals("testToken", auth.authToken());
        Assertions.assertEquals("testUsername", result.username(), "Expected TestUsername but got " + result.username());
    }

    @Test
    @DisplayName("Unsuccessful Register")
    public void RegisterFailure() throws ResponseException {
        UserData badUser = new UserData(null, null, null);
        var result = serve.registerRequest(badUser);
        ResponseException thrown = Assertions.assertThrows(ResponseException.class, result);
        Assertions.assertEquals("Error: bad request", thrown.getMessage());
        Assertions.assertEquals(400, thrown.statusCode());
    }

    @Test
    @DisplayName("Successful Login")
    public void LoginSuccess() throws ResponseException {
        AuthData result = serve.loginRequest(user);
        Assertions.assertEquals("testToken", auth.authToken());
        Assertions.assertEquals("testUsername", result.username(), "expected testUsername but got " + result.username());
    }

    @Test
    @DisplayName("Login Wrong password")
    public void loginWrongPassword() throws ResponseException {
        UserData wrongPasswordGuy = new UserData("testUsername", "WrongPassword!", null);
        AuthData result = serve.loginRequest(wrongPasswordGuy);
        ResponseException thrown = Assertions.assertThrows(ResponseException.class, result);
        Assertions.assertEquals("Error: bad request", thrown.getMessage());
        Assertions.assertEquals(400, thrown.statusCode());
    }

    @Test
    @DisplayName("Successful logout")
    public void logoutSuccess() throws ResponseException {
        serve.logoutRequest(auth.authToken());
        Assertions.assertNull(auth);
    }

    @Test
    @DisplayName("Logout wrong authToken")
    public void logoutFail() throws ResponseException {
        result = serve.logoutRequest(null);
        Assertions.assertThrows(ResponseException.class, result);
    }

    @Test
    @DisplayName("Successful Game List")
    public void GameListSuccess() throws ResponseException {
        ListGamesResult result = serve.gameListRequest(auth.authToken());
        Assertions.assertEquals(listGames, result);
    }

    @Test
    @DisplayName("Unsuccessful Game List")
    public void GameListFail() throws ResponseException {
       ListGamesResult result = serve.gameListRequest(null);
       Assertions.assertThrows(ResponseException.class, result);
    }

    @Test
    @DisplayName("Successful Create Game")
    public void CreateGameSuccess() throws ResponseException {
        GameData result = serve.createGameRequest(auth.authToken(), game1);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Unsuccessful Create Game")
    public void CreateGameFail() throws ResponseException {
        GameData result = serve.createGameRequest(wrongAuth, game1);
        Assertions.assertThrows(ResponseException.class, result);
    }

    @Test
    @DisplayName("Successful Join Game")
    public void JoinGameSuccess() throws ResponseException {
        serve.joinGameRequest(auth.authToken(), "WHITE", 1111);
        Assertions.assertEquals(auth.username(), game1.whiteUsername());
    }

    @Test
    @DisplayName("JoinGame Wrong Color")
    public void joinGameWrongColor() throws ResponseException {
        var result = serve.joinGameRequest(auth.authToken(), "PURPLE", 1111);
        Assertions.assertThrows(ResponseException.class, result);
    }

    @Test
    @DisplayName("JoinGame invalid ID")
    public void joinGameInvalidID () throws ResponseException {
        var result = serve.joinGameRequest(auth.authToken(), "WHITE", 12);
        Assertions.assertThrows(ResponseException.class, result);
    }

    @Test
    @DisplayName("Delete database success (only test)")
    public void deleteDBSuccess() {
        serve.deleteDataBase();
        Assertions.assertNull(dataaccess);
    }
}