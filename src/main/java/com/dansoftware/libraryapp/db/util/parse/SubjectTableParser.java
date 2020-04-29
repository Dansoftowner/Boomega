package com.dansoftware.libraryapp.db.util.parse;

import com.dansoftware.libraryapp.db.pojo.Subject;
import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * A SubjectTableParser can parse the data from the
 * SQL table of 'subjects'
 *
 * @see ResultSetParser
 */
public class SubjectTableParser implements ResultSetParser {

    private final String id = "id";
    private final String name = "name";

    @Override
    public void parse(ResultSet from, DataPackage to) throws SQLException {
        Objects.requireNonNull(from, "The resultSet 'from' cannot be null"::toString);
        Objects.requireNonNull(to, "The dataPackage 'to' cannot be null"::toString);

        while (from.next())
            to.getSubjects().add(new Subject(
                    from.getInt(id),
                    from.getString(name)
            ));
    }
}
