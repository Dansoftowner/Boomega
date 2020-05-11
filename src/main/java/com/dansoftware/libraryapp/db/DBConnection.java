package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A DBConnection can access a database and
 * create the {@link Connection} object.
 *
 * <p>
 * A DBConnection do all necessary work that every
 * database-connection object must do but a DBConnection
 * doesn't know how to actually retrieve the data from
 * the particular database.
 * <i>So, you have to use concretions such as {@link SQLiteConnection}</i>
 *
 * @author Daniel Gyorffy
 */
public abstract class DBConnection {

    private final String driver;
    private final String url;
    private Connection connection;

    /**
     * Creates a DBConnection that connects to the particular database
     * with the given jdbc-driver.
     *
     * @param driver           the JDBC driver's name; must not be null
     * @param url              the JDBC url; must not be null
     * @param exceptionHandler the consumer that handles
     *                         the sql exception that occurs
     *                         during the connection-creation;
     *                         must not be null
     * @throws NullPointerException if one of the parameters is null
     * @see Class#forName(String)
     * @see java.sql.DriverManager#getConnection(String)
     */
    protected DBConnection(String driver, String url, Consumer<SQLException> exceptionHandler) {
        this.driver = Objects.requireNonNull(driver, "The url mustn't be null");
        this.url = Objects.requireNonNull(url, "The driver's name mustn't be null");

        Objects.requireNonNull(exceptionHandler, "The exception-handler mustn't be null");
        try {
            this.connect();
        } catch (SQLException e) {
            exceptionHandler.accept(e);
        }
    }

    /**
     * Connects to the database and creates the jdbc-connection
     * object.
     *
     * <p>
     * To access the created {@link Connection} object
     * you have to call the {@link DBConnection#getConnection()}
     * method.
     *
     * @throws SQLException if some sql exception occurs during the execution.
     */
    protected void connect() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.connection = DriverManager.getConnection(url);
    }

    /**
     * Gives access to the jdbc-connection object.
     *
     * @return the Connection object
     */
    protected Connection getConnection() {
        return this.connection;
    }

    /**
     * Closes the database-connection
     *
     * @throws SQLException if some sql exception occurs during the execution.
     */
    protected void close() throws SQLException {
        this.connection.close();
    }

    /**
     * Returns a {@link Task} that loads all data (Authors, Books, Subjects, Publishers) from the database
     * and puts them into a DataPackage.
     *
     * @return the {@link DataPackage} that contains the data
     * @throws SQLException if some SQLException occurs during the execution
     */
    public abstract Task<DataPackage> loadAllData();

}