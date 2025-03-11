package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataBaseTests {
    private static MySQLDataBase sqlDB;
    UserData r = new UserData("testUser", "testPassword", "test@test.com");

    @BeforeEach
    public void init() {
        DatabaseManager.callCreate();
        sqlDB = new MySQLDataBase();
    }

    @AfterEach
    public void clearDB() {
        sqlDB.deleteFullDataBase();
    }

    @Test
    @DisplayName("tables created")
    public void testTablesCreated() {
        String created = "SHOW TABLES LIKE 'userData'";
        try (Connection conn = DatabaseManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(created)) {

            assertTrue(result.next(), "Table 'userData' does not exist");
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("successful insert user")
    public void insertUserSuccess() {
        String queryUser = "SELECT COUNT(*) FROM  userData WHERE username = 'testUser'";
        sqlDB.createUser(r);
        try (Connection conn = DatabaseManager.getConnection()) {
            Statement queryStatement = conn.createStatement();
            ResultSet result = queryStatement.executeQuery(queryUser);

            assertTrue(result.next(), "Error: adding user failed");
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Insert null username")
    public void insertNullUsername() {
        String queryUser = "SELECT COUNT(*) FROM  userData WHERE username = 'testUser'";
        sqlDB.createUser(r);
        try (Connection conn = DatabaseManager.getConnection()) {
            Statement queryStatement = conn.createStatement();
            ResultSet result = queryStatement.executeQuery(queryUser);

            assertTrue(result.next(), "Error: expected to not insert, but instead found" + result.next());
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("successful get user")
    public void getUserSuccess() {
        sqlDB.createUser(r);
        UserData actual = sqlDB.getUser(r.username());
        UserData expected = new UserData("testUser", "hashedPassword-cannotTest) ", "test@test.com");

        assertEquals(actual.username(), expected.username());
        assertEquals(actual.email(), expected.email());
    }

    @Test
    @DisplayName("unsuccessful get user")
    public void getUserFail() {
        sqlDB.createUser(r);
        UserData result = sqlDB.getUser("wrongUser");

        assertNull(result, "Error: expected null but returned " + result);
    }

    @Test
    @DisplayName("successful create auth")
    public void createAuthSuccess() {
        sqlDB.createUser(r);
        AuthData a = sqlDB.createAuth(r.username());

        assertNotNull(a.authToken());
    }

    @Test
    @DisplayName("failed create auth")
    public void createAuthFail() {
        sqlDB.createUser(r);
        AuthData a = sqlDB.createAuth("badUser");

        assertNull(a);
    }

    @Test
    @DisplayName("Successful get auth")
    public void getAuthSuccess() {
        sqlDB.createUser(r);
        AuthData a = sqlDB.createAuth(r.username());
        AuthData b = sqlDB.getAuthData(a.authToken());

        assertEquals(b, a);
    }

    @Test
    @DisplayName("Failed get auth")
    public void getAuthFail() {
        sqlDB.createUser(r);
        AuthData correctUser = sqlDB.createAuth(r.username());
        AuthData incorrect = sqlDB.getAuthData("Wrong auth");
        assertNotEquals(correctUser, incorrect);
    }

    @Test
    @DisplayName("Successful delete auth")
    public void deleteAuthSuccess() {
        sqlDB.createUser(r);
        AuthData a = sqlDB.createAuth(r.username());
        sqlDB.deleteAuth(a);
        AuthData shouldBeNull = sqlDB.getAuthData(a.authToken());
        assertNull(shouldBeNull);
    }

    @Test
    @DisplayName("Failed delete auth")
    public void deleteAuthFail() {
        sqlDB.createUser(r);
        AuthData a = sqlDB.createAuth(r.username());
        AuthData b = sqlDB.getAuthData(a.authToken());
        assertNotNull(b);
    }

    @Test
    @DisplayName("list games success")
    public void listGamesSuccess() {
        sqlDB.createUser(r);
        sqlDB.createGame("testGame1");
        sqlDB.createGame("testGame2");
        ArrayList<GameData> g = sqlDB.listGames();
        assertNotNull(g);

    }

    @Test
    @DisplayName("list game fail")
    public void listGamesFail() {
        sqlDB.createUser(r);
        ArrayList<GameData> a = sqlDB.listGames();
        assertTrue(a.isEmpty());
    }

    @Test
    @DisplayName("Create game success")
    public void createGameSuccess() {
        sqlDB.createUser(r);
        GameData g = sqlDB.createGame("testGame");
        GameData a = sqlDB.getGame(g.gameID());
        assertNotNull(a);
    }

    @Test
    @DisplayName("Create game fail")
    public void createGameFail() {
        sqlDB.createUser(r);
        GameData a = sqlDB.getGame(1);
        assertNull(a);
    }

    @Test
    @DisplayName("Update game success")
    public void updateGameSuccess() {
        sqlDB.createUser(r);
        GameData g = sqlDB.createGame("testGame");
        sqlDB.updateGameData("BLACK", g, r.username());
        GameData a = sqlDB.getGame(g.gameID());
        assertEquals(a.blackUsername(), r.username());
    }

    @Test
    @DisplayName("Update game fail")
    public void updateGameFail() {
        sqlDB.createUser(r);
        GameData g = sqlDB.createGame("testGame");
        sqlDB.updateGameData("GREEN", g, r.username());
        GameData a = sqlDB.getGame(g.gameID());
        assertNotEquals(a.blackUsername(), r.username());
    }

    @Test
    @DisplayName("Delete full database success")
    public void deleteDBSuccess() {
        sqlDB.createUser(r);
        GameData g = sqlDB.createGame("testGame");
        sqlDB.deleteFullDataBase();
        GameData a = sqlDB.getGame(g.gameID());
        assertNull(a);
    }

    @Test
    @DisplayName("Delete full database fail")
    public void deleteDBFail() {
        sqlDB.createUser(r);
        GameData a = sqlDB.createGame("testGame");
        GameData g = sqlDB.getGame(a.gameID());
        assertNotNull(g);
    }
}
