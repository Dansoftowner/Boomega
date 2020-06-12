package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.main.VersionInfo;
import com.dansoftware.libraryapp.update.loader.Loader;
import com.dansoftware.libraryapp.update.notifier.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * An UpdateSearcher can search for updates.
 *
 * <p>
 * If new update is available
 * the UpdateSearcher notifies
 * to user about that.
 *
 * @author Daniel Gyorffy
 */
public class UpdateSearcher {

    private final VersionInfo base;
    private final Loader loader;
    private final Notifier notifier;

    /**
     * Creates a basic update searcher object;
     *
     * @param base the base version that the object should compare to; mustn't be null
     * @param loader the object that loads the information about the update; mustn't be null
     * @param notifier the object that is responsible for notifying the user about the
     *                 new update/some error; mustn't be null
     * @throws NullPointerException if one of the arguments is null.
     */
    public UpdateSearcher(VersionInfo base, Loader loader, Notifier notifier) {
        this.base = Objects.requireNonNull(base, "The base mustn't be null");
        this.loader = Objects.requireNonNull(loader, "The loader mustn't be null");
        this.notifier = Objects.requireNonNull(notifier, "The notifier mustn't be null");
    }

    /**
     * Decides that there is new update available and notifies {@link Notifier} object about it.
     */
    public void search() {
        try {
            var information = loader.load();
            if (information != null) {
                boolean newUpdate = base.compareTo(new VersionInfo(information.getVersion())) < 0;
                if (newUpdate) notifier.notifyUpdate(information);
            }
        } catch (Exception e) {
            notifier.notifyException(e);
        }
    }
}
