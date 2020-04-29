package com.dansoftware.libraryapp.db.util.parse;

import com.dansoftware.libraryapp.db.pojo.*;
import com.dansoftware.libraryapp.db.util.DataPackage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Objects;

/**
 * A JoinedTableParser can parse the data from the
 * SQL view of 'books_joined'
 *
 * @see ResultSetParser
 */
public final class JoinedTableParser implements ResultSetParser {

    // Book columns ------------------------>
    private final String book_id = "BookId";
    private final String book_title = "title";
    private final String published_year = "published_year";
    private final String book_lang = "lang";
    private final String book_notes = "notes";
    private final String book_isbn = "isbn";
    private final String book_number_of_copies = "number_of_copies";
    private final String book_number_of_pages = "number_of_pages";

    // Author columns ---------------------->
    private final String author_id = "author_id";
    private final String author_name = "author_name";
    private final String author_description = "author_description";

    // Publisher columns -------------------->
    private final String publisher_id = "publisher_id";
    private final String publisher_name = "publisher_name";

    //Subject columns ----------------------->
    private final String subject_id = "subject_id";
    private final String subject_name = "subject_name";


    @Override
    public void parse(ResultSet from, DataPackage to) throws SQLException {
        Objects.requireNonNull(from, "The resultSet 'from' cannot be null"::toString);
        Objects.requireNonNull(to, "The dataPackage 'to' cannot be null"::toString);

        while (from.next()) {

            Book actualBook = new Book.Builder()
                    .id(from.getInt(book_id))
                    .title(from.getString(book_title))
                    .publishedYear(from.getInt(published_year))
                    .language(from.getString(book_lang))
                    .language(from.getString(book_notes))
                    .isbn(from.getString(book_isbn))
                    .numberOfPages(from.getInt(book_number_of_pages))
                    .numberOfCopies(from.getInt(book_number_of_copies))
                    .authors(new LinkedList<>())
                    .build();

            DecimalFormat decimalFormat = new DecimalFormat("00");

            for (int i = 1; i <= Book.MAX_AUTHOR_COUNT; i++) {
                String id_column = author_id + decimalFormat.format(i);

                Author actualAuthor = new Author(
                        from.getInt(id_column),
                        from.getString(author_name),
                        from.getString(author_description)
                );

                actualBook.getAuthors().add(actualAuthor);
                to.getAuthors().add(actualAuthor);
            }

            Subject actualSubject = new Subject(from.getInt(subject_id), from.getString(subject_name));
            Publisher actualPublisher = new Publisher(from.getInt(publisher_id), from.getString(publisher_name));

            actualBook.setSubject(actualSubject);
            actualBook.setPublisher(actualPublisher);

            to.getBooks().add(actualBook);
            to.getSubjects().add(actualSubject);
            to.getPublishers().add(actualPublisher);
        }
    }
}
