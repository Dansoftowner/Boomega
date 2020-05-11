package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import com.dansoftware.libraryapp.db.util.JDBCUtils;
import com.dansoftware.libraryapp.db.util.SqliteURLGenerator;
import com.dansoftware.libraryapp.db.util.parse.*;
import com.dansoftware.libraryapp.gui.util.concurrency.LocalizedTask;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
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
     * @param databaseFile     the sqlite-database file to connect to;
     *                         must not be null
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

    /**
     * This method reads some resource (SQL Script) from the classpath
     * and executes them on the database connection.
     *
     * <p>
     * This is important because it creates all necessary tables and views
     * if they are not exists yet.
     */
    private void createTablesAndViews() {
        //executing the table creator script
        try (var input = getClass().getResourceAsStream("/com/dansoftware/libraryapp/db/create_tables.sql")) {
            JDBCUtils.executeSqlScript(getConnection(), input);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        //executing the view creator script
        try (var input = getClass().getResourceAsStream("/com/dansoftware/libraryapp/db/create_views.sql")) {
            JDBCUtils.executeSqlScript(getConnection(), input);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Task<DataPackage> loadAllData() {
        return new LocalizedTask<>() {
            @Override
            protected DataPackage call() throws SQLException {
                DataPackage dataPackage = new DataPackage();

                String sql;
                ResultSetParser parser;

                this.updateLocalizedTitle("db.connection.load.title");
                this.updateMessage("0%");
                this.updateProgress(0, 100);


                //Selecting all the books and other records that are can be joined to the books table
                sql = "SELECT * FROM books_joined;";
                parser = new JoinedTableParser();
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
                     ResultSet resultSet = preparedStatement.executeQuery()) {

                    parser.parse(resultSet, dataPackage);
                }

                this.updateMessage("25%");
                this.updateProgress(25, 100);

                //selecting the authors that are can't be joined with the books table
                sql = "SELECT * FROM single_authors";
                parser = new AuthorTableParser();
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
                     ResultSet resultSet = preparedStatement.executeQuery()) {

                    parser.parse(resultSet, dataPackage);
                }

                this.updateMessage("50%");
                this.updateProgress(50, 100);

                //selecting the publishers that are can't be joined with the books table
                sql = "SELECT * FROM single_publishers";
                parser = new PublisherTableParser();
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
                     ResultSet resultSet = preparedStatement.executeQuery()) {

                    parser.parse(resultSet, dataPackage);
                }

                this.updateMessage("75%");
                this.updateProgress(75, 100);

                //selecting the subjects that are can't be joined with the books table
                sql = "SELECT * FROM single_subjects";
                parser = new SubjectTableParser();
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
                     ResultSet resultSet = preparedStatement.executeQuery()) {

                    parser.parse(resultSet, dataPackage);
                }

                this.updateMessage("100%");
                this.updateProgress(100, 100);

                return dataPackage;
            }
        };
    }

}
