package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.processor.DatabaseFactory;
import com.dansoftware.libraryapp.db.processor.FailListener;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.exceptions.ErrorMessage;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.dansoftware.libraryapp.locale.I18N.getAlertMsg;
import static java.util.Objects.isNull;

/**
 * A NitriteDatabase is a {@link Database} that basically wraps the
 * Nitrite NoSQL database api.
 *
 * @see Nitrite
 * @author Daniel Gyorffy
 */
public class NitriteDatabase implements Database {

    private List<Book> cache;

    private final DatabaseMeta databaseMeta;
    private final Nitrite dbImpl;
    private final Credentials credentials;

    public NitriteDatabase(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) throws SecurityException, NitriteIOException {
        this.credentials = Objects.requireNonNull(credentials, "The Credentials must not be null!");
        this.dbImpl = init(databaseMeta, credentials);
        this.databaseMeta = databaseMeta;
    }

    private Nitrite init(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) throws SecurityException, NitriteIOException {
        //account data
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        File file = databaseMeta.getFile();

        //preparing the NitriteBuilder
        NitriteBuilder builder = Nitrite.builder().compressed().filePath(file);

        Nitrite nitrite = credentials.isAnonymous() ?
                builder.openOrCreate() : builder.openOrCreate(username, password);
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
    public DatabaseMeta getMeta() {
        return this.databaseMeta;
    }

    /**
     * Returns a {@link DatabaseFactory} that can be used for creating/opening a {@link NitriteDatabase} through the
     * {@link com.dansoftware.libraryapp.db.processor.LoginProcessor} API.
     *
     * @see com.dansoftware.libraryapp.db.processor.LoginProcessor
     * @return the factory object
     */
    public static DatabaseFactory factory() {
        return (databaseMeta, credentials, failListener) -> {
            try {
                return new NitriteDatabase(databaseMeta, credentials);
            } catch (SecurityException e) {
                String title = getAlertMsg("login.auth.failed.security.title");
                String message = getAlertMsg("login.auth.failed.security.msg");

                failListener.onFail(title, message);
            } catch (NitriteIOException e) {
                String title = getAlertMsg("login.auth.failed.io.title");
                String message = getAlertMsg("login.auth.failed.io.msg");

                failListener.onFail(title, message, e);
            }

            return null;
        };
    }
}
