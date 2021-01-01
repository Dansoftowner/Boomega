package com.dansoftware.libraryapp.db;

import com.dansoftware.libraryapp.db.auth.DatabaseAuthenticator;
import com.dansoftware.libraryapp.db.auth.FailListener;
import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.Magazine;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.exceptions.ErrorMessage;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;
import org.dizitart.no2.objects.ObjectFilter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.dansoftware.libraryapp.locale.I18N.getLoginViewValue;
import static java.util.Objects.isNull;

/**
 * A NitriteDatabase is a {@link Database} that basically wraps the
 * Nitrite NoSQL database api.
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 */
public class NitriteDatabase implements Database {

    private List<Book> bookCache;
    private List<Magazine> magazineCache;

    private final DatabaseMeta databaseMeta;
    private final Nitrite dbImpl;

    public NitriteDatabase(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) throws SecurityException, NitriteIOException {
        Objects.requireNonNull(credentials, "The Credentials must not be null!");
        this.dbImpl = init(databaseMeta, credentials);
        this.databaseMeta = databaseMeta;
    }

    private Nitrite init(@NotNull DatabaseMeta databaseMeta,
                         @NotNull Credentials credentials) throws SecurityException, NitriteIOException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        File file = databaseMeta.getFile();

        NitriteBuilder builder = Nitrite.builder().filePath(file);
        Nitrite nitrite = credentials.isAnonymous() ?
                builder.openOrCreate() : builder.openOrCreate(username, password);
        if (nitrite == null)
            throw new NitriteIOException(ErrorMessage.IMPORT_READ_ERROR);
        return nitrite;
    }

    @Override
    public void insertMagazine(@NotNull Magazine magazine) {
        this.dbImpl.getRepository(Magazine.class).insert(magazine);
    }

    @Override
    public void updateMagazine(@NotNull Magazine magazine) {
        this.dbImpl.getRepository(Magazine.class).update(magazine);
    }

    @Override
    public void removeMagazine(@NotNull Magazine magazine) {
        this.dbImpl.getRepository(Magazine.class).remove(magazine);
    }

    @Override
    public List<Magazine> getMagazines(boolean fromCache) {
        if (fromCache) return magazineCache = isNull(magazineCache) ? getMagazines(false) : magazineCache;
        return this.dbImpl.getRepository(Magazine.class).find().toList();
    }

    @Override
    public List<Magazine> getMagazines(FindOptions findOptions) {
        return this.dbImpl.getRepository(Magazine.class).find(findOptions).toList();
    }

    @Override
    public List<Magazine> getMagazines(ObjectFilter objectFilter, FindOptions findOptions) {
        return this.dbImpl.getRepository(Magazine.class).find(objectFilter, findOptions).toList();
    }

    @Override
    public void insertBook(@NotNull Book book) {
        this.dbImpl.getRepository(Book.class).insert(book);
    }

    @Override
    public void updateBook(@NotNull Book book) {
        this.dbImpl.getRepository(Book.class).update(book);
    }

    @Override
    public void removeBook(@NotNull Book book) {
        this.dbImpl.getRepository(Book.class).remove(book);
    }

    @Override
    public List<Book> getBooks(boolean fromCache) {
        if (fromCache) return bookCache = isNull(bookCache) ? getBooks(false) : bookCache;
        return this.dbImpl.getRepository(Book.class).find().toList();
    }

    @Override
    public List<Book> getBooks(FindOptions findOptions) {
        return this.dbImpl.getRepository(Book.class).find(findOptions).toList();
    }

    @Override
    public List<Book> getBooks(ObjectFilter objectFilter, FindOptions findOptions) {
        return this.dbImpl.getRepository(Book.class).find(objectFilter, findOptions).toList();
    }

    @Override
    public void clearCache() {
        this.bookCache.clear();
        this.bookCache = null;
        this.magazineCache.clear();
        this.magazineCache = null;
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

    public static DatabaseAuthenticator getAuthenticator() {
        return new DatabaseAuthenticatorImpl();
    }

    private static final class DatabaseAuthenticatorImpl extends DatabaseAuthenticator {

        @Override
        protected Database create(@NotNull DatabaseMeta databaseMeta,
                                  @NotNull Credentials credentials,
                                  @NotNull FailListener failListener) {
            try {
                return new NitriteDatabase(databaseMeta, credentials);
            } catch (SecurityException e) {
                String title;
                String message;
                if (credentials.isAnonymous()) {
                    title = getLoginViewValue("login.auth.failed.emptycredentials.title");
                    message = getLoginViewValue("login.auth.failed.emptycredentials.msg");
                } else {
                    title = getLoginViewValue("login.auth.failed.security.title");
                    message = getLoginViewValue("login.auth.failed.security.msg");
                }
                failListener.onFail(title, message);
            } catch (NitriteIOException e) {
                String title;
                String message;
                if (e.getErrorMessage().getErrorCode().equals("NO2.2012")) {
                    title = getLoginViewValue("login.auth.failed.io.exists.title");
                    message = getLoginViewValue("login.auth.failed.io.exists.msg", databaseMeta.getFile());
                } else {
                    title = getLoginViewValue("login.auth.failed.io.title");
                    message = getLoginViewValue("login.auth.failed.io.msg");
                }
                failListener.onFail(title, message, e);
            }

            return null;
        }
    }
}
