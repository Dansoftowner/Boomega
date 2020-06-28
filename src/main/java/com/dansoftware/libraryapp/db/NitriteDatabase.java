package com.dansoftware.libraryapp.db;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.exceptions.ErrorMessage;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;

import java.io.File;
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
 * <p>
 * If you create a nitrite database with an account that doesn't includes the filePath,
 * it will create an in-memory database.
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 * @see Account#anonymous()
 */
public class NitriteDatabase implements Database {

    private List<Book> cache;

    private final Nitrite dbImpl;
    private final Account account;
    private String name;

    public NitriteDatabase(Account account) throws SecurityException, NitriteIOException {
        this.account = Objects.requireNonNull(account, "The account must not be null!");
        this.dbImpl = init(account);
    }

    public NitriteDatabase(String name, Account account) {
        this(account);
        this.name = name;
    }

    private Nitrite init(Account account) throws SecurityException, NitriteIOException {
        //account data
        String username = account.getUsername();
        String password = account.getPassword();
        File file = account.getFile();

        //preparing the NitriteBuilder
        NitriteBuilder builder = Nitrite.builder().compressed().filePath(file);

        Nitrite nitrite = account.isAnonymous() ? builder.openOrCreate() : builder.openOrCreate(username, password);
        if (isNull(nitrite))
            throw new NitriteIOException(ErrorMessage.IMPORT_READ_ERROR);

        return nitrite;
    }

    @Override
    public void insertBook(Book book) {
        this.dbImpl.getRepository(Book.class).insert(book);
    }

    @Override
    public void updateBook(Book book) {
        this.dbImpl.getRepository(Book.class).update(book);
    }

    @Override
    public void removeBook(Book book) {
        this.dbImpl.getRepository(Book.class).remove(book);
    }

    @Override
    public List<Book> getBooks(boolean fromCache) {
        if (fromCache) {
            return cache = isNull(cache) ? getBooks(false) : cache;
        }

        return this.dbImpl.getRepository(Book.class).find().toList();
    }

    @Override
    public void clearCache() {
        this.cache.clear();
        this.cache = null;
    }

    @Override
    public boolean isClosed() {
        return this.dbImpl.isClosed();
    }

    @Override
    public void close() {
        this.dbImpl.close();
    }

    @Override
    public DatabaseMetadata getMetadata() {
        return new DatabaseMetadata(this.name, this.account);
    }

    public Account getAccount() {
        return account;
    }
}
