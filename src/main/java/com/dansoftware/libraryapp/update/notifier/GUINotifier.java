package com.dansoftware.libraryapp.update.notifier;

import com.dansoftware.libraryapp.gui.updateview.UpdateActivity;
import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.application.Platform;

public class GUINotifier extends LoggerNotifier {
    @Override
    public void notifyUpdate(UpdateInformation updateInformation) {
        super.notifyUpdate(updateInformation);
        Platform.runLater(() -> {
            UpdateActivity updateActivity = new UpdateActivity(updateInformation);
            updateActivity.show();
        });
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
