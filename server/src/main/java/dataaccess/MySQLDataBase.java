package dataaccess;
import chess.ChessGame;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.util.*;
import java.sql.*;

public class MySQLDataBase extends DatabaseManager implements DataAccess {
    public MySQLDataBase() {
        createTables();
    }

    private static void createTables() {
        /**
         * this string creates the table for all the user data
         * including username, email, and password
         */
        String createUserDataTable = """
                CREATE TABLE IF NOT EXISTS userData (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULl
                )
                """;
        /**
         * This string creates the table for a single authToken
         */
        String createAuthDataTable = """
                CREATE TABLE IF NOT EXISTS authData (
                auth_id INT AUTO_INCREMENT PRIMARY KEY,
                auth_token VARCHAR(255) UNIQUE NOT NULl
                )
                """;
        /**
         * This string creates the table for joining
         * a single username with multiple authTokens
         */
        String createJoinUserAuthTable = """
                CREATE TABLE IF NOT EXISTS joinUserAuth (
                user_id INT NOT NULL,
                auth_id INT NOT NULL,
                PRIMARY KEY (user_id, auth_id),
                FOREIGN KEY (user_id) REFERENCES userData (id) ON DELETE CASCADE,
                FOREIGN KEY (auth_id) REFERENCES authData (auth_id) ON DELETE CASCADE
                )
                """;
        /**
         * This string creates the table for all gameData
         */
        String createGameDataTable = """
                CREATE TABLE IF NOT EXISTS gameData (
                game_id INT PRIMARY KEY,
                white_username VARCHAR(50),
                black_username VARCHAR(50),
                game_name VARCHAR(50),
                game BLOB NOT NULL
                )
                """;


        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
             stmt.execute(createUserDataTable);
             stmt.execute(createAuthDataTable);
             stmt.execute(createJoinUserAuthTable);
             stmt.execute(createGameDataTable);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());

        }
    }

    public UserData getUser(String username) {
        String getUser = "SELECT username, email, password FROM userData WHERE username = ?";
    try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(getUser)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new UserData(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("email")
                );
            }
            throw new DataAccessException("Error: Could not find user_data with username " + username);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void createUser(UserData r) {
        String insertUser = "INSERT INTO userData (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement statement = conn.prepareStatement(insertUser)) {
            String hashedPassword = hashPassword(r.password());
            statement.setString(1, r.username());
            statement.setString(2, r.email());
            statement.setString(3, hashedPassword);
            int success = statement.executeUpdate();
            didDatabaseExecute(success);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private Boolean unHashedPassword(String password) {
        String hashed = hashPassword(password);
        return BCrypt.checkpw(password, hashed);
    }

    public AuthData createAuth(String username) {
        String randomGeneratedAuth = generateAuthToken();
        String insertAuth = "INSERT INTO authData (auth_token) VALUES (?)";
        String queryAuth = "SELECT * FROM authData WHERE auth_token = ?";
        String getUserID = "SELECT id FROM userData WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement authStatement = conn.prepareStatement(insertAuth)) {
            authStatement.setString(1, randomGeneratedAuth);
            int authSuccess = authStatement.executeUpdate();
            didDatabaseExecute(authSuccess);
            PreparedStatement queryStatement = conn.prepareStatement(queryAuth);
            queryStatement.setString(1, randomGeneratedAuth);
            ResultSet resultSet = queryStatement.executeQuery();
            if (resultSet.next()) {
                int authID = resultSet.getInt("auth_id");
                 try (PreparedStatement userStatement = conn.prepareStatement(getUserID)) {
                     userStatement.setString(1, username);
                     ResultSet userResult = userStatement.executeQuery();
                     if (userResult.next()) {
                         int userID = userResult.getInt("id");
                         insertJoinUserAuth(conn, userID, authID);
                         return new AuthData(username, randomGeneratedAuth);
                     }
                 }
                 }
            throw new DataAccessException("user does not exist!");
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private void insertJoinUserAuth(Connection conn, int userID, int authID) {
        String insertJoin = "INSERT INTO joinUserAuth (user_id, auth_id) VALUES (?, ?)";
        try (PreparedStatement joinStatement = conn.prepareStatement(insertJoin)) {
            joinStatement.setInt(1, userID);
            joinStatement.setInt(2, authID);
            int joinSuccess = joinStatement.executeUpdate();
            didDatabaseExecute(joinSuccess);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    public AuthData getAuthData(String auth) {
        String getAuthToken = "SELECT auth_id FROM authData WHERE auth_token = ?";
        String getAuthData = """
        SELECT u.username, a.auth_token FROM joinUserAuth j
        JOIN userData u ON j.user_id = u.id
        JOIN authData a ON j.auth_id = a.auth_id
        WHERE j.auth_id = ?
    """;
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement authTokenStatement = conn.prepareStatement(getAuthToken)) {
            authTokenStatement.setString(1, auth);
            ResultSet authTokens = authTokenStatement.executeQuery();
            if (!authTokens.next()) {
                throw new DataAccessException("auth_data does not exist for auth_token" + auth);
            }
            int authID = authTokens.getInt("auth_id");
            try (PreparedStatement authDataStatement = conn.prepareStatement(getAuthData)) {
                authDataStatement.setInt(1, authID);
                ResultSet authData = authDataStatement.executeQuery();
                if (authData.next()) {
                return new AuthData(
                        authData.getString("username"),
                        authData.getString("auth_token")
                );
                }
            }
            throw new DataAccessException("Unable to retrieve the authData!");
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void deleteAuth(AuthData a) {
        String getAuth = "SELECT auth_id FROM authData WHERE auth_token = ?";
        String removeAuth = "DELETE FROM joinUserAuth WHERE auth_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement authStatement = conn.prepareStatement(getAuth)) {
            authStatement.setString(1, a.authToken());
            ResultSet authSet = authStatement.executeQuery();
            if (! authSet.next()) {
                throw new DataAccessException("Provided authToken does not exist!");
            }
            int authID = authSet.getInt("auth_id");
            try (PreparedStatement removeStatement = conn.prepareStatement(removeAuth)) {
                removeStatement.setInt(1, authID);
                int removed = removeStatement.executeUpdate();
                didDatabaseExecute(removed);
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    public ArrayList<GameData> listGames() {
        String getGameList = "SELECT * FROM gameData";
        ArrayList<GameData> gameDataList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement getGame = conn.prepareStatement(getGameList)) {
            ResultSet gameSet = getGame.executeQuery();
            while (gameSet.next()) {
                ChessGame game = deserializeGame(gameSet.getBytes("game"));
                GameData newGame = new GameData(
                        gameSet.getInt("id"),
                        gameSet.getString("white_username"),
                        gameSet.getString("black_username"),
                        gameSet.getString("game_name"),
                        game
                );
                gameDataList.add(newGame);
            }
            return gameDataList;
    } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static ChessGame deserializeGame(byte[] gameBytes) {
        if (gameBytes == null) return null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(gameBytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (ChessGame) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GameData createGame(String gameName) {
        int randInt = generateRandomInt();
        String createGame = """
                INSERT INTO gameData (game_id, white_username,
                black_username, game_name, game)
                VALUES = (?, ?, ?, ?, ?)""";
        ChessGame chessGame = new ChessGame();
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement game = conn.prepareStatement(createGame)) {
            game.setInt(1, randInt);
            game.setString(2, null);
            game.setString(3, null);
            game.setString(4, gameName);
            game.setObject(5, chessGame);
            int inserted = game.executeUpdate();
            didDatabaseExecute(inserted);
            return new GameData(randInt, null, null, gameName, chessGame);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private Integer generateRandomInt() {
        String isIntInGameData = "SELECT * FROM gameData WHERE game_id = ?";
        Random random = new Random();
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement getGameData = conn.prepareStatement(isIntInGameData)) {
            while (true) {
                int randInt = 1000 + random.nextInt();
                getGameData.setInt(1, randInt);
                return randInt;
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public GameData getGame(int gameID) {
        String getGame = "SELECT FROM GameData WHERE game_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement gameStatement = conn.prepareStatement(getGame)) {
            gameStatement.setInt(1,gameID);
            ResultSet gameResult = gameStatement.executeQuery();
            if (gameResult.next()) {
                ChessGame game = deserializeGame(gameResult.getBytes("game"));
                return new GameData(
                        gameID,
                        gameResult.getString("white_username"),
                        gameResult.getString("black_username"),
                        gameResult.getString("game_name"),
                        game
                );
            }
            throw new DataAccessException("Error: Could not find the game with game_id " + gameID);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void updateGameData(String playerColor, GameData game, String username) {
        String selectGame = "SELECT FROM gameData WHERE user_id = ?";
        String insertWhiteUser = """
                INSERT INTO gameData (white_username) VALUES (?)
                """;
        String insertBlackUser = """
                INSERT INTO gameData (black_username) VALUES (?)
                """;
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement selectStatement = conn.prepareStatement(selectGame)) {
            selectStatement.setInt(1, game.gameID());
            ResultSet selectResult = selectStatement.executeQuery();
            if (selectResult.next()) {
                if (playerColor.equals("WHITE")) {
                    PreparedStatement insertWhite = conn.prepareStatement(insertWhiteUser);
                    insertWhite.setString(1, username);
                    int whiteSuccess = insertWhite.executeUpdate();
                    didDatabaseExecute(whiteSuccess);
                }
                else {
                    PreparedStatement insertBlack = conn.prepareStatement(insertBlackUser);
                    insertBlack.setString(1, username);
                    int blackSuccess = insertBlack.executeUpdate();
                    didDatabaseExecute(blackSuccess);
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteFullDataBase() {
        String dropUser = "DROP TABLE userData";
        String dropAuth = "DROP TABLE authData";
        String dropGame = "DROP TABLE gameData";
        try (Connection conn = DatabaseManager.getConnection()) {
            disableFK(conn);
            PreparedStatement userStatement = conn.prepareStatement(dropUser);
            userStatement.executeUpdate();
            PreparedStatement authStatement = conn.prepareStatement(dropAuth);
            authStatement.executeUpdate();
            PreparedStatement gameStatement = conn.prepareStatement(dropGame);
            gameStatement.executeUpdate();
            enableFK(conn);
        } catch (SQLException |DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    private void disableFK(Connection conn) {
        String disable = "SET FOREIGN_KEY_CHECKS = 0";
        try {
            PreparedStatement disabledStatement = conn.prepareStatement(disable);
            disabledStatement.executeUpdate();
    } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void enableFK(Connection conn) {
        String enable = "SET FOREIGN_KEY_CHECKS = 1";
        try {
            PreparedStatement enabledStatement = conn.prepareStatement(enable);
            enabledStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addGame(GameData existingGame) {
        String createGame = """
                INSERT INTO gameData (game_id, white_username,
                black_username, game_name, game)
                VALUES = (?, ?, ?, ?, ?)""";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement game = conn.prepareStatement(createGame)) {
            game.setInt(1, existingGame.gameID());
            game.setString(2, existingGame.whiteUsername());
            game.setString(3, existingGame.blackUsername());
            game.setString(4, existingGame.gameName());
            game.setObject(5, existingGame.game());
            int inserted = game.executeUpdate();
            didDatabaseExecute(inserted);
        } catch (SQLException | DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    private void didDatabaseExecute(int success) throws DataAccessException {
        if (success == 0) {
            throw new DataAccessException("insert/update/delete failed");
        }
    }
}
