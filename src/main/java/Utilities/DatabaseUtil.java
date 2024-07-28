package Utilities;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.commandline.CommandLineUtils;

import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.exception.LiquibaseException;
import liquibase.database.DatabaseFactory;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseUtil {

    private static final String PROPERTIES_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    private static String dbName;
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds delay for retries

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            if (input == null) {
                throw new IOException("Unable to find " + PROPERTIES_FILE);
            }
            properties.load(input);
            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");
            dbName = properties.getProperty("db.name");
            Class.forName(driver);

            // Create database if it doesn't exist
            createDatabaseIfNotExists();

            // Run Liquibase migrations
            runLiquibaseMigrations();
        } catch (IOException | ClassNotFoundException | SQLException | LiquibaseException e) {
            LOGGER.severe("Error initializing DatabaseUtil: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        String urlWithoutDb = url;
        try (Connection connection = DriverManager.getConnection(urlWithoutDb, user, password);
             Statement statement = connection.createStatement()) {

            // Check if database exists
            try (var resultSet = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'")) {
                if (!resultSet.next()) {
                    // Database does not exist, so create it
                    statement.executeUpdate("CREATE DATABASE " + dbName);
                    LOGGER.info("Database created successfully.");
                } else {
                    LOGGER.info("Database already exists.");
                }
            }
        }
        // Update URL to include database name after creation
        url = urlWithoutDb + dbName;
    }

    private static void runLiquibaseMigrations() throws LiquibaseException {
        // Retry logic for Liquibase migrations
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                DatabaseConnection dbConnection = new JdbcConnection(connection);
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(dbConnection);

                ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
                Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", resourceAccessor, database);

                liquibase.update("");
                LOGGER.info("Liquibase migrations applied successfully.");
                return; // Exit if migration succeeds
            } catch (Exception e) {
                attempts++;
                LOGGER.severe("Error running Liquibase migrations on attempt " + attempts + ": " + e.getMessage());
                if (attempts >= MAX_RETRIES) {
                    throw new RuntimeException("Failed to run Liquibase migrations after " + MAX_RETRIES + " attempts.", e);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        SQLException lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                lastException = e;
                if ("3D000".equals(e.getSQLState())) { // PostgreSQL error code for "Invalid Catalog Name" (database does not exist)
                    LOGGER.warning("Database does not exist. Attempting to create it.");
                    try {
                        createDatabaseIfNotExists();
                        LOGGER.info("Database created successfully.");
                    } catch (SQLException createException) {
                        LOGGER.severe("Failed to create database: " + createException.getMessage());
                        throw createException;
                    }
                } else {
                    LOGGER.severe("Failed to connect to database: " + e.getMessage());
                    throw e;
                }
            }
        }
        LOGGER.severe("Failed to connect to database after " + MAX_RETRIES + " attempts.");
        throw lastException;
    }
}
