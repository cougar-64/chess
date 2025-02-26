package service;

import dataaccess.DataAccess;
import dataaccess.InMemoryDA;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import model.*;
import java.util.ArrayList;

public class ServiceTests {
    private static UserData user;
    private static UserData userForRegister;
    private static AuthData authForRegister;
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
        userForRegister = new UserData("existing", "existing", "existing@exist.com");
        user = new UserData("testUsername", "testPassword", "test@test.com");
        dataaccess = new InMemoryDA();
        serve = new Service(dataaccess);
        authForRegister = new AuthData("testUsername", "testToken");
        game1 = new GameData(1234, null, null, "testName", null);
        game2 = new GameData(1111, null, null, "game2", null);
        game3 = new GameData(4432, null, null, "game3", null);
        ArrayList<GameData> gamedata = new ArrayList<>();
        gamedata.add(game3);
        gamedata.add(game2);
        gamedata.add(game1);
        listGames = new ListGamesResult(gamedata);
        dataaccess.addGame(game3);
        dataaccess.addGame(game2);
        dataaccess.addGame(game1);

        wrongAuth = "wrongAuthToken";
    }

    @BeforeEach
    public void register() throws ResponseException {
        auth = serve.registerRequest(user);

    }

    @Test
    @DisplayName("Successful Register")
    public void registerSuccess() throws ResponseException {
        AuthData result = serve.registerRequest(userForRegister);
        Assertions.assertEquals("testToken", authForRegister.authToken());
        Assertions.assertEquals("existing", result.username(),
                "Expected TestUsername but got " + result.username());
    }

    @Test
    @DisplayName("Unsuccessful Register")
    public void registerFailure() throws ResponseException {
        UserData badUser = new UserData(null, null, null);
        Assertions.assertThrows(ResponseException.class, () ->
        {serve.registerRequest(badUser);});
    }

    @Test
    @DisplayName("Successful Login")
    public void loginSuccess() throws ResponseException {
        AuthData result = serve.loginRequest(user);
        Assertions.assertEquals("testUsername", result.username(),
                "expected testUsername but got " + result.username());
    }

    @Test
    @DisplayName("Login Wrong password")
    public void loginWrongPassword() {
        UserData wrongPasswordGuy = new UserData("testUsername", "WrongPassword!", null);
        ResponseException thrown = Assertions.assertThrows(ResponseException.class, () ->
        {serve.loginRequest(wrongPasswordGuy);});
        Assertions.assertEquals("Error: unauthorized", thrown.getMessage());
        Assertions.assertEquals(401, thrown.statusCode());
    }

    @Test
    @DisplayName("Successful logout")
    public void logoutSuccess() throws ResponseException {
        serve.logoutRequest(auth.authToken());
        Assertions.assertNotNull(auth);
    }

    @Test
    @DisplayName("Logout wrong authToken")
    public void logoutFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () ->
        {serve.logoutRequest(null);});
    }

    @Test
    @DisplayName("Successful Game List")
    public void gameListSuccess() throws ResponseException {
        ListGamesResult result = serve.gameListRequest(auth.authToken());
        Assertions.assertEquals(listGames, result);
    }

    @Test
    @DisplayName("Unsuccessful Game List")
    public void gameListFail() throws ResponseException {
       Assertions.assertThrows(ResponseException.class, () ->
       {serve.gameListRequest("WrongAuth");});
    }

    @Test
    @DisplayName("Successful Create Game")
    public void createGameSuccess() throws ResponseException {
        GameData result = serve.createGameRequest(auth.authToken(), game1);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Unsuccessful Create Game")
    public void createGameFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {serve.createGameRequest(wrongAuth, game1);});
    }

    @Test
    @DisplayName("Successful Join Game")
    public void joinGameSuccess() throws ResponseException {
        serve.joinGameRequest(auth.authToken(), "WHITE", 1234);
        Assertions.assertNotEquals(auth.username(), game1.whiteUsername());
    }

    @Test
    @DisplayName("JoinGame Wrong Color")
    public void joinGameWrongColor() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () ->
        {serve.joinGameRequest(auth.authToken(), "PURPLE", 1111);
        });
    }

    @Test
    @DisplayName("JoinGame invalid ID")
    public void joinGameInvalidID () throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () ->
        {serve.joinGameRequest(auth.authToken(), "WHITE", 12);});
    }

    @Test
    @DisplayName("Delete database success (only test)")
    public void deleteDBSuccess() {
        serve.deleteDataBase();
        Assertions.assertNotNull(dataaccess);
    }
}