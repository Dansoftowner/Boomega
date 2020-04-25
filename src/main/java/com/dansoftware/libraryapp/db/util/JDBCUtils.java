package com.dansoftware.libraryapp.db.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public static void executeSqlScript(Connection connection, InputStream resourceInputStream) throws NullPointerException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(resourceInputStream);

        try (var reader = new BufferedReader(new InputStreamReader(resourceInputStream))) {
            StringBuilder actualStatement = new StringBuilder();

            String actualReadLine;
            while ((actualReadLine = reader.readLine()) != null) {
                actualStatement.append(actualReadLine);

                if (actualReadLine.endsWith(";")) {

                    try (PreparedStatement preparedStatement = connection.prepareStatement(actualStatement.toString())) {
                        preparedStatement.execute();
                    }

                    actualStatement = new StringBuilder();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
