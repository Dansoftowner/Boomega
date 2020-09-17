package com.dansoftware.libraryapp.db.processor;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * A LoginProcessor is used for authenticating and creating/opening databases.
 *
 * <p>
 * A {@link LoginProcessor} requires a {@link DatabaseFactory} for creating the particular {@link Database}
 * and a {@link FailListener} (optional) for handling the exceptions.
 *
 * <p>
 * Example for creating a {@link com.dansoftware.libraryapp.db.NitriteDatabase}
 * <pre>
 *     var dbMeta = new DatabaseMeta(new File("example.db"));
 *     var dbCredentials = new Credentials("username", "*****");
 *
 *     Database database = new LoginProcessor(NitriteDatabase.factory())
 *             .onFailed((title, message, t) -> {
 *                   logger.error(message, t);
 *             }).process(dbMeta, dbCredentials);
 *
 *     //using the database
 * </pre>
 *
 * @author Daniel Gyorffy
 * @see LoginProcessor
 * @see DatabaseFactory
 * @see Database
 * @see FailListener
 */
@Deprecated
public class LoginProcessor {

    private FailListener onFailListener;
    private final DatabaseFactory databaseFactory;

    private LoginProcessor(@NotNull DatabaseFactory databaseFactory) {
        this.databaseFactory = Objects.requireNonNull(databaseFactory, "The DatabaseFactory shouldn't be null");
    }

    public Optional<FailListener> getOnFailListener() {
        return Optional.ofNullable(this.onFailListener);
    }

    public LoginProcessor onFailed(@Nullable FailListener onFailListener) {
        this.onFailListener = onFailListener;
        return this;
    }

    /**
     * Creates/Opens the particular database that doesn't need credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @return the {@link Database} object
     */
    @Nullable
    public Database process(DatabaseMeta databaseMeta) {
        return this.process(databaseMeta, Credentials.anonymous());
    }

    /**
     * Creates/Opens the particular database.
     *
     * <p>
     * If the database is not created yet, a database will be created with the given credentials.
     * Otherwise it will try to open the database with the credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @param credentials  the object that holds the credentials
     * @return the {@link Database} object.
     */
    @Nullable
    public Database process(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) {
        return databaseFactory.create(databaseMeta, credentials, getOnFailListener().orElse(FailListener.empty()));
    }

    /**
     * Creates the particular database if not exists yet.
     *
     * @param databaseMeta the meta-information of the database
     * @return {@code true} if the database is created successfully; {@code false} otherwise.
     */
    public boolean touch(@NotNull DatabaseMeta databaseMeta) {
        return this.touch(databaseMeta, Credentials.anonymous());
    }

    /**
     * Creates the particular database if not exists yet with credentials.
     *
     * @param databaseMeta the meta-information of the database
     * @param credentials  the object that holds the credentials
     * @return {@code true} if the database is created successfully; {@code false} otherwise.
     */
    public boolean touch(@NotNull DatabaseMeta databaseMeta, @NotNull Credentials credentials) {
        Database created;
        if ((created = this.process(databaseMeta, credentials)) != null) {
            created.close();
            return true;
        }

        return false;
    }

    public static LoginProcessor of(DatabaseFactory databaseFactory) {
        return new LoginProcessor(databaseFactory);
    }
}
