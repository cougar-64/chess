package client;

import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    static ServerFacade facade;
    private static Server server;
    private static HashMap<Integer, GameData> gameData = new HashMap<>();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearGameData() {
    }


    @Test
    public void registerSuccess() throws Exception {
        var authData = facade.register("testUser", "testPassword", "testEmail");
        assertNotNull(authData.authToken());
    }

    @Test
    public void registerFail() {
        Exception exception = assertThrows(ResponseException.class, () -> {facade.register("testUser", "testPassword", null);});
        assertEquals("Error: bad request", exception.getMessage());

    }

    @Test
    public void loginSuccess() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginFailure() {
        Exception exception = assertThrows(ResponseException.class, () -> {facade.login(null, null);});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void logoutSuccess() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        facade.logout(authData.authToken());
        Exception exception = assertThrows(ResponseException.class, () -> {facade.create(authData.authToken(), "testGame");});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void logoutFailure() {
        Exception exception = assertThrows(ResponseException.class, () -> {facade.logout("badAuth");});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void createSuccess() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        facade.create(authData.authToken(), "testGame");
        var gameList = facade.listGames(authData.authToken());
        assertFalse(gameList.games().isEmpty());
    }

    @Test
    public void createFailure() {
        Exception exception = assertThrows(ResponseException.class, () -> {facade.create("badAuth", "testGame");});
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void listSuccess() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        GameData game = facade.create(authData.authToken(), "testGame");
        GameData game2 = facade.create(authData.authToken(), "testGame2");
        GameData game3 = facade.create(authData.authToken(), "testGame3");
        gameData.put(1, game);
        gameData.put(2, game2);
        gameData.put(3, game3);
        var listGames = facade.listGames(authData.authToken());
        assertNotNull(listGames.games());
    }

    @Test
    public void listNoGames() throws Exception {
        facade.clear();
        var authData = facade.register("testUser", "testPassword", "testEmail");
        var listGames = facade.listGames(authData.authToken());
        assertTrue(listGames.games().isEmpty());
    }

    @Test
    public void joinSuccess() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        GameData game = facade.create(authData.authToken(), "testGame");
        gameData.put(1, game);
        facade.join(authData.authToken(), "1", "WHITE", gameData);
        var listGames = facade.listGames(authData.authToken());
        game = listGames.games().get(1);
        assertTrue(game.whiteUsername().equals("testUser"));
    }

    @Test
    public void joinColorTaken() throws Exception {
        var authData = facade.login("testUser", "testPassword");
        GameData game = facade.create(authData.authToken(), "testGame");
        gameData.put(1, game);
        facade.join(authData.authToken(), "1", "WHITE", gameData);
        var listGames = facade.listGames(authData.authToken());
        Exception exception = assertThrows(ResponseException.class, () -> {facade.join(authData.authToken(), "1", "WHITE", gameData);});
        assertEquals("Error: already taken", exception.getMessage());
    }


}
