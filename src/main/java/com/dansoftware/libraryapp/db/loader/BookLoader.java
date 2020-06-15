package com.dansoftware.libraryapp.db.loader;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.Book;

import java.util.List;

public class BookLoader extends DataLoader<Book> {

    public BookLoader(Database database) {
        super(database);
    }

    @Override
    protected List<Book> load(boolean fromCache) {
        return this.getDatabase()
                .getDbImpl()
                .getRepository(Book.class)
                .find()
                .toList();
    }
}
