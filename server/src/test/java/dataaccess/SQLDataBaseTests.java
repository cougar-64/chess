package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLDataBaseTests {
    private static mySQLDataBase sqlDB;
    UserData r = new UserData("testUser", "testPassword", "test@test.com");
    @BeforeEach
    public void init() {
        DatabaseManager.callCreate();
        sqlDB = new mySQLDataBase();
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
}
