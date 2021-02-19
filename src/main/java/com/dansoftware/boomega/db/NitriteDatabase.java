package com.dansoftware.boomega.db;

import com.dansoftware.boomega.db.auth.DatabaseAuthenticator;
import com.dansoftware.boomega.db.auth.FailListener;
import com.dansoftware.boomega.db.data.Record;
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

import static com.dansoftware.boomega.i18n.I18N.getValue;

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

    private final ObjectRepository<Record> recordRepository;

    public NitriteDatabase(@NotNull DatabaseMeta databaseMeta,
                           @NotNull Credentials credentials)
            throws SecurityException, NitriteIOException {
        Objects.requireNonNull(credentials, "The Credentials must not be null!");
        this.nitriteClient = init(databaseMeta, credentials);
        this.databaseMeta = databaseMeta;
        this.recordRepository = nitriteClient.getRepository("BoomegaRecords", Record.class);
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
    public void insertRecord(@NotNull Record record) {
        this.recordRepository.insert(record);
    }

    @Override
    public void updateRecord(@NotNull Record record) {
        this.recordRepository.update(record);
    }

    @Override
    public void removeRecord(@NotNull Record record) {
        this.recordRepository.remove(record);
    }

    @Override
    public int getTotalRecordCount() {
        return this.recordRepository.find().totalCount();
    }

    @Override
    public List<Record> getRecords() {
        return this.recordRepository.find().toList();
    }

    @Override
    public List<Record> getRecords(@NotNull FindOptions findOptions) {
        return this.recordRepository.find(findOptions).toList();
    }

    @Override
    public List<Record> getRecords(@NotNull ObjectFilter objectFilter, @NotNull FindOptions findOptions) {
        return this.recordRepository.find(objectFilter, findOptions).toList();
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
                    title = getValue("login.auth.failed.emptycredentials.title");
                    message = getValue("login.auth.failed.emptycredentials.msg");
                } else {
                    title = getValue("login.auth.failed.security.title");
                    message = getValue("login.auth.failed.security.msg");
                }
                failListener.onFail(title, message);
            } catch (NitriteIOException e) {
                String title;
                String message;
                if (e.getErrorMessage().getErrorCode().equals("NO2.2012")) {
                    title = getValue("login.auth.failed.io.exists.title");
                    message = getValue("login.auth.failed.io.exists.msg", databaseMeta.getFile());
                } else {
                    title = getValue("login.auth.failed.io.title");
                    message = getValue("login.auth.failed.io.msg");
                }
                failListener.onFail(title, message, e);
            }

            return null;
        }
    }
}
