package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import com.dansoftware.libraryapp.db.util.JDBCUtils;
import com.dansoftware.libraryapp.db.util.SqliteURLGenerator;
import com.dansoftware.libraryapp.db.util.parse.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * An SQLiteConnection is a {@link DBConnection} that can connect
 * and retrieve data from an sqlite database file.
 *
 * @author Daniel Gyorffy
 */
public class SQLiteConnection extends DBConnection {

    /**
     * Creates an SQLIteConnection that connects to the
     * sqlite database file.
     *
     * @param databaseFile the sqlite-database file to connect to;
     *             must not be null
     * @param exceptionHandler the consumer that handles
     *                         the sql exception that occurs
     *                         during the connection-creation;
     *                         must not be null
     * @throws NullPointerException if one of the parameters is null
     * @see Class#forName(String)
     * @see DriverManager#getConnection(String)
     */
    protected SQLiteConnection(File databaseFile, Consumer<SQLException> exceptionHandler) {
        super("org.sqlite.JDBC", new SqliteURLGenerator(databaseFile).getJDBCUrl(), exceptionHandler);
        this.createTablesAndViews();
    }

    private void createTablesAndViews() {
        try(var input = getClass().getResourceAsStream("/com/dansoftware/libraryapp/db/create_tables.sql")) {
            JDBCUtils.executeSqlScript(getConnection(), input);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        try(var input = getClass().getResourceAsStream("/com/dansoftware/libraryapp/db/create_views.sql")) {
            JDBCUtils.executeSqlScript(getConnection(), input);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public DataPackage loadAllData() throws SQLException {
        DataPackage dataPackage = new DataPackage();

        String sql;
        ResultSetParser parser;

        sql = "SELECT * FROM books_joined;";
        parser = new JoinedTableParser();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            parser.parse(resultSet, dataPackage);
        }

        sql = "SELECT * FROM single_authors";
        parser = new AuthorTableParser();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            parser.parse(resultSet, dataPackage);
        }

        sql = "SELECT * FROM single_publishers";
        parser = new PublisherTableParser();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            parser.parse(resultSet, dataPackage);
        }

        sql = "SELECT * FROM single_subjects";
        parser = new SubjectTableParser();
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            parser.parse(resultSet, dataPackage);
        }

        return dataPackage;
    }

}
