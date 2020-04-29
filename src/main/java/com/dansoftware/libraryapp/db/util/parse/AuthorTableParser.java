package com.dansoftware.libraryapp.db.util.parse;

import com.dansoftware.libraryapp.db.pojo.Author;
import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * A AuthorTableParser can parse the data from the
 * SQL table of 'authors'
 *
 * @see ResultSetParser
 */
public class AuthorTableParser implements ResultSetParser {

    private final String id = "id";
    private final String name = "name";
    private final String description = "description";

    @Override
    public void parse(ResultSet from, DataPackage to) throws SQLException {
        Objects.requireNonNull(from, "The resultSet 'from' cannot be null"::toString);
        Objects.requireNonNull(to, "The dataPackage 'to' cannot be null"::toString);

        while (from.next())
            to.getAuthors().add(new Author(
                    from.getInt(id),
                    from.getString(name),
                    from.getString(description)
            ));

    }
}
