package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.pojo.Author;
import com.dansoftware.libraryapp.db.pojo.Book;
import com.dansoftware.libraryapp.db.pojo.Publisher;
import com.dansoftware.libraryapp.db.pojo.Subject;
import com.dansoftware.libraryapp.db.util.DataBaseFileRecognizer;
import com.dansoftware.libraryapp.db.util.JDBCURLGenerator;
import com.dansoftware.libraryapp.db.util.RecordCollection;
import com.dansoftware.libraryapp.db.util.SqliteURLGenerator;
import com.dansoftware.libraryapp.log.GuiLog;

import java.io.File;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for communicating with the database.
 * A concretion of the {@link AbstractDBConnection} class.
 *
 * @author Daniel Gyorffy
 */
public final class DBConnection extends AbstractDBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    /**
     * The single-instance holder field
     */
    private static DBConnection instance;

    /**
     * This constant represents the path on the Class-Path of the sql script that creates the necessary tables in the database
     */
    private final static String CREATE_TABLES_SCRIPT = "/com/dansoftware/libraryapp/db/create_tables.sql";

    /**
     * This constant represents the path on the Class-Path of the sql script that creates the necessary views in the database
     */
    private final static String CREATE_VIEWS_SCRIPT = "/com/dansoftware/libraryapp/db/create_views.sql";

    /**
     * Defines the class-name of the JDBC Driver
     */
    private final static String JDBC_DRIVER = "org.sqlite.JDBC";

    /**
     * The file object of the db file
     */
    private static File databaseFile;

    /**
     * Don't let anyone to create an instance of this class.
     *
     * @throws SQLException if some sql exception occurs
     */
    private DBConnection() throws SQLException {
    }


    public DataPackage loadAllData() throws SQLException {

        RecordCollection<Author> authors = new RecordCollection<>();
        RecordCollection<Book> books = new RecordCollection<>();
        RecordCollection<Publisher> publishers = new RecordCollection<>();
        RecordCollection<Subject> subjects = new RecordCollection<>();

        DataPackage dataPackage = new DataPackage(authors, publishers, subjects, books);

        String sql = "SELECT * FROM books_joined;";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            

        }

        return null;
    }

    @Override
    protected InputStream getTableCreatorScriptStream() {
        return getClass().getResourceAsStream(CREATE_TABLES_SCRIPT);
    }

    @Override
    protected InputStream getViewCreatorScriptStream() {
        return getClass().getResourceAsStream(CREATE_VIEWS_SCRIPT);
    }

    @Override
    protected String getJDBCDriver() {
        return JDBC_DRIVER;
    }

    @Override
    protected JDBCURLGenerator getJDBCUrlMaker() {
        return new SqliteURLGenerator(databaseFile = new DataBaseFileRecognizer().getDBFile());
    }

    /**
     * This method creates an instance of this class and handles the {@link SQLException}
     *
     * @return the database connection object
     */
    private static DBConnection createDBConnectionObject() {
        try {
            return new DBConnection();
        } catch (SQLException e) {
            LOGGER.log(new GuiLog(Level.SEVERE, e, "dbconnection.failed", new Object[]{ databaseFile.getName() }));
        }

        return null;
    }

    /**
     * This static method creates the DBConnection if it isn't created yet, and
     * returns it.
     *
     * @return the single instance of the DBConnection class
     */
    public static DBConnection getInstance() {
        if (instance == null)
            instance = createDBConnectionObject();

        return instance;
    }

    public static class DataPackage {
        private Collection<Author> authors;
        private Collection<Publisher> publishers;
        private Collection<Subject> subjects;
        private Collection<Book> books;

        private DataPackage(Collection<Author> authors, Collection<Publisher> publishers, Collection<Subject> subjects, Collection<Book> books) {
            this.authors = authors;
            this.publishers = publishers;
            this.subjects = subjects;
            this.books = books;
        }

        public Collection<Author> getAuthors() {
            return authors;
        }

        public Collection<Publisher> getPublishers() {
            return publishers;
        }

        public Collection<Subject> getSubjects() {
            return subjects;
        }

        public Collection<Book> getBooks() {
            return books;
        }
    }
}
