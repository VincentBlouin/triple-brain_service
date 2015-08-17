/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.usage_log;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {
    static Connection connection;

    @Inject
    private static DataSource dataSource;

    public static PreparedStatement preparedStatement(String query) {
        try {
            return staleConnectionProofGetter().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void closeConnection() throws SQLException {
        staleConnectionProofGetter().close();
    }

    public static void clearDatabases() {
        try {
            String query = "DROP TABLE IF EXISTS usage_log;";
            preparedStatement(query).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createTables() {
        createTablesUsingDataSource(dataSource);
    }

    public static void createTablesUsingDataSource(DataSource dataSource) {
        String query = "CREATE TABLE usage_log (\n" +
                "    action_date TIMESTAMP NOT NULL,\n" +
                "    username  VARCHAR(100),\n" +
                "    user_action VARCHAR(255) NOT NULL,\n" +
                ");";
        try {
            dataSource.getConnection().createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Connection getConnection(){
        return staleConnectionProofGetter();
    }

    private static Connection staleConnectionProofGetter() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
        } catch (SQLException ex) {
            connection = createConnection();
            ex.printStackTrace();
        }
        return connection;
    }

    private static Connection createConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
