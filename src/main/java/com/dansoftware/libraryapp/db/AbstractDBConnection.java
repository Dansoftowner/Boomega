package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.util.DataPackage;
import com.dansoftware.libraryapp.db.util.JDBCURLGenerator;
import com.dansoftware.libraryapp.db.util.JDBCUtils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * An AbstractDBConnection can connect to a particular database.
 *
 * <p>
 * @author Daniel Gyorffy
 */
public abstract class AbstractDBConnection {

    /**
     * Contains the database connection object
     */
    private Connection connection;

    /**
     * This constructor creates the database connection besides constructs the
     * object.
     *
     * @throws SQLException if some some exception occurs while trying to create the database connection
     */
    protected AbstractDBConnection() throws SQLException {
        createConnection();
        createTablesAndViews();
    }

    /**
     * This method creates the database connection and initialize the {@link AbstractDBConnection#connection} field.
     */
    private void createConnection() throws SQLException {
        try {
            Class.forName(getJDBCDriver());
            JDBCURLGenerator urlGenerator = getJDBCUrlMaker();
            connection = DriverManager.getConnection(urlGenerator.getJDBCUrl());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This method creates the necessary tables and views in the database
     */
    private void createTablesAndViews() {
        JDBCUtils.executeSqlScript(connection, getTableCreatorScriptStream());
        JDBCUtils.executeSqlScript(connection, getViewCreatorScriptStream());
    }

    /**
     * This method closes the database connection
     *
     * @throws Exception if some exception occurs
     */
    public final void close() throws Exception {
        this.connection.close();
    }

    public abstract DataPackage loadAllData() throws SQLException;

    /**
     * Getter for the {@link AbstractDBConnection#connection} field.
     * @return the {@link Connection} object
     */
    protected final Connection getConnection() {
        return connection;
    }

    /**
     * This method should return the input stream from the sql script
     * that contains the table creations.
     *
     * @return the input stream of the sql script
     */
    protected abstract InputStream getTableCreatorScriptStream();

    /**
     * This method should return the input stream from the sql script
     * that contains the view creations.
     *
     * @return the input stream of the sql script
     */
    protected abstract InputStream getViewCreatorScriptStream();

    /**
     * This method should return the JDBC Driver's class name
     *
     * @return the class name with the package 'prefix'. For example: "org.example.DRIVER"
     */
    protected abstract String getJDBCDriver();

    /**
     * This method should return a {@link JDBCURLGenerator} object that
     * creates the right jdbc url.
     *
     * @return the JDBC url generator object.
     */
    protected abstract JDBCURLGenerator getJDBCUrlMaker();

}
