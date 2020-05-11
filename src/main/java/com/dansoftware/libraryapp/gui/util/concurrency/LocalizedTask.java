package com.dansoftware.libraryapp.gui.util.concurrency;

import com.dansoftware.libraryapp.locale.Bundles;
import javafx.concurrent.Task;

/**
 * A LocalizedTask is a {@link Task} that can produce localized
 * messages from the {@link Bundles#getProgressMessages()}
 * bundle.
 *
 * @param <V> the result type returned by this FutureTask's get methods
 * @see Bundles#getProgressMessages()
 * @see Task
 */
public abstract class LocalizedTask<V> extends Task<V> {

    protected void updateLocalizedMessage(String key) {
        super.updateMessage(Bundles.getProgressMessages().getString(key));
    }

    protected void updateLocalizedTitle(String key) {
        super.updateMessage(Bundles.getProgressMessages().getString(key));
    }
}
