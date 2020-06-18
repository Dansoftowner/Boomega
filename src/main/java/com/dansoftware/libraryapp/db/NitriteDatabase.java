package com.dansoftware.libraryapp.db;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.exceptions.SecurityException;
import org.dizitart.no2.objects.ObjectRepository;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * A NitriteDatabase is a {@link Database} that basically wraps the
 * Nitrite NoSQL database api.
 *
 * <p>
 * You can use the {@link DatabaseFactory} for constructing a Nitrite-database.
 * <br><br> <b>Example:</b>
 * <pre>{@code
 *    //defining the account
 *    Account account = new Account("username", "password", "path/to/file");
 *
 *    //creating the database object
 *    Database db = DatabaseFactory.getDatabase("nitrite", account);
 *
 *    //you can cast it to NitriteDatabase if you want
 *    NitriteDatabase nitriteDb = (NitriteDatabase) db;
 * }</pre>
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 */
public class NitriteDatabase implements Database {

    private List<Book> cache;

    private final Nitrite dbImpl;
    private final ObjectRepository<Book> bookRepository;
    private final Account account;

    public NitriteDatabase(Account account) throws InvalidAccountException {
        this.account = Objects.requireNonNull(account, "The account must not be null!");
        this.dbImpl = init(account);
        this.bookRepository = dbImpl.getRepository(Book.class);
    }

    private Nitrite init(Account account) throws InvalidAccountException {
        //account data
        String username = account.getUsername();
        String password = account.getPassword();
        String filePath = account.getFilePath();

        //preparing the NitriteBuilder
        NitriteBuilder nitriteBuilder = Nitrite.builder().compressed().filePath(filePath);

        try {
            return account.isAnonymous() ? nitriteBuilder.openOrCreate() : nitriteBuilder.openOrCreate(username, password);
        } catch (SecurityException e) {
            throw new InvalidAccountException(e);
        }
    }

    @Override
    public void insertBook(Book book) {
        this.bookRepository.insert(book);
    }

    @Override
    public void updateBook(Book book) {
        this.bookRepository.update(book);
    }

    @Override
    public void removeBook(Book book) {
        this.bookRepository.remove(book);
    }

    @Override
    public List<Book> getBooks(boolean fromCache) {
        if (fromCache) {
            return cache = isNull(cache) ? getBooks(false) : cache;
        }

        return this.bookRepository.find().toList();
    }

    @Override
    public void clearCache() {
        this.cache.clear();
        this.cache = null;
    }
}
