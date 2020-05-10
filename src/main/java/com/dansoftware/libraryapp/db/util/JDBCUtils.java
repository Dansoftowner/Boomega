package com.dansoftware.libraryapp.db.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This class contains some utilities for database handling.
 */
public class JDBCUtils {

    /**
     * Don't let anyone to create an instance of this class
     */
    private JDBCUtils() {
    }

    /**
     * This method method reads the resource with given input stream,
     * and executes the sql statements with the Connection. In the resource the statements should
     * be separated with a semicolon (;)
     *
     * @param resourceInputStream the input stream of the resource
     * @param connection          the Connection object that we want to execute on
     * @throws NullPointerException if the {@param connection} or the {@param resourceInputStream} is null.
     */
    public static void executeSqlScript(Connection connection, InputStream resourceInputStream) throws IOException, SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(resourceInputStream);

        var reader = new BufferedReader(new InputStreamReader(resourceInputStream));
        var actualStatement = new StringBuilder();

        int actualReadChar;
        while ((actualReadChar = reader.read()) != -1) {
            char asChar = (char) actualReadChar;
            actualStatement.append(asChar);

            if (actualReadChar == ';') {

                try(var preparedStatement = connection.prepareStatement(actualStatement.toString())) {
                    preparedStatement.execute();
                }

                actualStatement = new StringBuilder();

            }
        }
    }
}
