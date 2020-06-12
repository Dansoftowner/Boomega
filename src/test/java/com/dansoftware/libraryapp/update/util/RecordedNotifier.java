package com.dansoftware.libraryapp.update.util;

import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.libraryapp.update.notifier.Notifier;

public class RecordedNotifier implements Notifier {

    private boolean updateNotified;
    private boolean exceptionNotified;

    @Override
    public void notifyUpdate(UpdateInformation updateInformation) {
        this.updateNotified = true;
    }

    @Override
    public void notifyException(Throwable throwable) {
        this.exceptionNotified = true;
    }

    public boolean isUpdateNotified() {
        return updateNotified;
    }

    public boolean isExceptionNotified() {
        return exceptionNotified;
    }
}
