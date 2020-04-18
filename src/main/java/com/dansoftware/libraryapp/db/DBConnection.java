package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfiguration;
import com.dansoftware.libraryapp.log.GuiLog;
import com.dansoftware.libraryapp.main.ApplicationArgumentHandler;

import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static DBConnection instance;

    private final static String URL_PREFIX = "jdbc:sqlite:";
    private final static String JDBC_DRIVER = "org.sqlite.JDBC";

    private Connection connection;

    private DBConnection() {
        connect();
        createTablesAndViews(connection);
    }

    private void connect() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(getJDBCUrl());
        } catch (SQLException e) {
            LOGGER.log(new GuiLog(Level.SEVERE, e, "", null));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTablesAndViews(Connection connection) {
        executeSqlScript("/com/dansoftware/libraryapp/db/create_tables.sql", connection);
        executeSqlScript("/com/dansoftware/libraryapp/db/create_views.sql", connection);
    }

    private String getJDBCUrl() {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        StringBuilder jdbcUrlBuilder = new StringBuilder(URL_PREFIX);

        Optional<File> launchedFile = ApplicationArgumentHandler.getLaunchedFile();
        Optional<String> customConfiguredDB =
                Optional.ofNullable(configurationHandler.getConfiguration(PredefinedConfiguration.CUSTOM_DB_FILE.getKey()));

        if(launchedFile.isPresent()) {
            jdbcUrlBuilder.append(launchedFile.get());
        } else if (customConfiguredDB.isPresent()) {
            jdbcUrlBuilder.append(customConfiguredDB.get());
        } else {
            jdbcUrlBuilder.append(applicationDataFolder.getConfigurationFile());
        }

        return jdbcUrlBuilder.toString();
    }

    private void executeSqlScript(String resource, Connection connection) {
        if (connection == null)
            return;

        try(var reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resource)))) {

            StringBuilder actualStatement = new StringBuilder();
            String actualReadLine;
            while((actualReadLine = reader.readLine()) != null) {
                actualStatement.append(actualReadLine);

                if (actualReadLine.endsWith(";")){
                    PreparedStatement preparedStatement = connection.prepareStatement(actualStatement.toString());
                    preparedStatement.execute();
                    preparedStatement.close();

                    actualStatement = new StringBuilder();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

    public static DBConnection getInstance() {
        if (instance == null) instance = new DBConnection();

        return instance;
    }

}
