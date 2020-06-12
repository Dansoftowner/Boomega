package com.dansoftware.libraryapp.update.notifier;

import com.dansoftware.libraryapp.update.UpdateInformation;

/**
 * A Notifier is responsible for notifying the user about a new update or an error
 */
public interface Notifier {
    void notifyUpdate(UpdateInformation updateInformation);
    void notifyException(Throwable throwable);
}
