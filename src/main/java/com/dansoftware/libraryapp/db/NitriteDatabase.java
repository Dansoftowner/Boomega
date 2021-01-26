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
import org.dizitart.no2.objects.ObjectRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.dansoftware.libraryapp.i18n.I18N.getLoginViewValue;

/**
 * A NitriteDatabase is a {@link Database} that basically wraps the
 * Nitrite NoSQL database api.
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 */
public class NitriteDatabase implements Database {

    private final DatabaseMeta databaseMeta;
    private final Nitrite nitriteClient;

    private final ObjectRepository<Book> bookRepository;
    private final ObjectRepository<Magazine> magazineRepository;

    public NitriteDatabase(@NotNull DatabaseMeta databaseMeta,
                           @NotNull Credentials credentials)
            throws SecurityException, NitriteIOException {
        Objects.requireNonNull(credentials, "The Credentials must not be null!");
        this.nitriteClient = init(databaseMeta, credentials);
        this.databaseMeta = databaseMeta;
        this.bookRepository = nitriteClient.getRepository(Book.class);
        this.magazineRepository = nitriteClient.getRepository(Magazine.class);
    }

    private Nitrite init(@NotNull DatabaseMeta databaseMeta,
                         @NotNull Credentials credentials)
            throws SecurityException, NitriteIOException {
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
        this.magazineRepository.insert(magazine);
    }

    @Override
    public void updateMagazine(@NotNull Magazine magazine) {
        this.magazineRepository.update(magazine);
    }

    @Override
    public void removeMagazine(@NotNull Magazine magazine) {
        this.magazineRepository.remove(magazine);
    }

    @Override
    public List<Magazine> getMagazines() {
        return this.magazineRepository.find().toList();
    }

    @Override
    public List<Magazine> getMagazines(FindOptions findOptions) {
        return this.magazineRepository.find(findOptions).toList();
    }

    @Override
    public List<Magazine> getMagazines(ObjectFilter objectFilter, FindOptions findOptions) {
        return this.magazineRepository.find(objectFilter, findOptions).toList();
    }

    @Override
    public void insertBook(@NotNull Book book) {
        this.bookRepository.insert(book);
    }

    @Override
    public void updateBook(@NotNull Book book) {
        this.bookRepository.update(book);
    }

    @Override
    public void removeBook(@NotNull Book book) {
        this.bookRepository.remove(book);
    }

    @Override
    public int getTotalMagazineCount() {
        return this.magazineRepository.find().totalCount();
    }

    @Override
    public int getTotalBookCount() {
        return this.bookRepository.find().totalCount();
    }

    @Override
    public List<Book> getBooks() {
        return this.bookRepository.find().toList();
    }

    @Override
    public List<Book> getBooks(FindOptions findOptions) {
        return this.bookRepository.find(findOptions).toList();
    }

    @Override
    public List<Book> getBooks(ObjectFilter objectFilter, FindOptions findOptions) {
        return this.bookRepository.find(objectFilter, findOptions).toList();
    }

    @Override
    public boolean isClosed() {
        return this.nitriteClient.isClosed();
    }

    @Override
    public void close() {
        this.nitriteClient.close();
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
