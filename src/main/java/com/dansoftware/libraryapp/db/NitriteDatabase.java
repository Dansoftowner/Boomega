package com.dansoftware.libraryapp.db;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.exceptions.SecurityException;
import org.dizitart.no2.objects.ObjectRepository;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class NitriteDatabase implements Database {

    private List<Book> booksCache;

    private final Nitrite dbImpl;
    private final ObjectRepository<Book> bookRepository;

    private final Account account;

    public NitriteDatabase(Account account) throws InvalidAccountException {
        this.account = Objects.requireNonNull(account, "The account must not be null!");

        try {
            this.dbImpl = initNitriteDb(account);
        } catch (SecurityException e) {
            throw new InvalidAccountException(e);
        }

        this.bookRepository = this.dbImpl.getRepository(Book.class);
    }

    private Nitrite initNitriteDb(Account account)
            throws org.dizitart.no2.exceptions.SecurityException {

        NitriteBuilder nitriteBuilder = Nitrite.builder()
                .compressed()
                .filePath(account.getFilePath());

        if (account.isAnonymous()) {
            return nitriteBuilder.openOrCreate();
        } else {
            var username = account.getUsername();
            var password = account.getPassword();

            return nitriteBuilder.openOrCreate(username, password);
        }
    }

    @Override
    public void insertBook(Book book) {

    }

    @Override
    public void updateBook(Book book) {

    }

    @Override
    public void removeBook(Book book) {

    }

    @Override
    public List<Book> getBooks(boolean fromCache) {
        return this.bookRepository.find().toList();
    }

    @Override
    public void clearCache() {
        this.booksCache.clear();
        this.booksCache = null;
    }
}
