package com.dansoftware.libraryapp.db.processor;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A DatabaseFactory used by {@link LoginProcessor}s for creating a particular
 * {@link Database} implementation.
 *
 * <p>
 * A {@link DatabaseFactory} should also notify the {@link FailListener} when
 * some exception occurs.
 *
 * @author Daniel Gyorffy
 */
public interface DatabaseFactory {

    /**
     * Creates the particular {@link Database} implementation.
     *
     * @param databaseMeta the meta-information about the database
     * @param credentials  the database credential
     * @param failListener the {@link FailListener} that is used when some exception occurs.
     * @return the {@link Database} object.
     */
    @Nullable
    Database create(@NotNull DatabaseMeta databaseMeta,
                    @NotNull Credentials credentials,
                    @NotNull FailListener failListener);
}
