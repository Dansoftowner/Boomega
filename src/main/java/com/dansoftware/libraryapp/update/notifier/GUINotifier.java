package com.dansoftware.libraryapp.update.notifier;

import com.dansoftware.libraryapp.update.UpdateInformation;

public class GUINotifier extends LoggerNotifier {
    @Override
    public void notifyUpdate(UpdateInformation updateInformation) {
        super.notifyUpdate(updateInformation);

        /*Notification.create()
                .level(NotificationLevel.INFO)
                .title("update.searcher.title")
                .msg("update.searcher.available")
                .eventHandler(event -> new UpdateDisplayer().display(updateInformation))
                .show();*/
    }

    @Override
    public void notifyException(Throwable throwable) {
        super.notifyException(throwable);

        /*Notification.create()
                .level(NotificationLevel.ERROR)
                .title("update.searcher.title")
                .msg("update.searcher.error")
                .cause(throwable)
                .show();*/
    }
}
