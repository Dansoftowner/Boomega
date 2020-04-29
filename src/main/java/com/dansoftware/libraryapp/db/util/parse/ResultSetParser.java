package com.dansoftware.libraryapp.db.util.parse;

import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A ResultSetParser can read and parse the data from a {@link ResultSet}
 * and it can send the parsed data to a particular {@link DataPackage}
 *
 * <p>
 * ResultSetParsers are designed to parse SQL database tables
 */
public interface ResultSetParser {

    /**
     * Reads and parses the data from the result set and then sends the
     * parsed data to the data package.
     *
     * @param from to read and parse from
     * @param to to write to
     * @throws SQLException if some SQL exception occurs during the
     *                      execution of this method
     */
    void parse(ResultSet from, DataPackage to) throws SQLException;
}
