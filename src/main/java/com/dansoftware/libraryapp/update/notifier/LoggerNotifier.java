package com.dansoftware.libraryapp.update.notifier;

import com.dansoftware.libraryapp.update.UpdateInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerNotifier implements Notifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNotifier.class);

    @Override
    public void notifyUpdate(UpdateInformation updateInformation) {
        LOGGER.info("New update is available: {}", updateInformation.getVersion());
    }

    @Override
    public void notifyException(Throwable throwable) {
        LOGGER.error("Couldn't pull the information about the update", throwable);
    }
}
