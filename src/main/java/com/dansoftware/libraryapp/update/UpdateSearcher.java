package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;
import com.dansoftware.libraryapp.gui.update.UpdateDisplayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

import static com.dansoftware.libraryapp.main.Globals.BUILD_INFO;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSearcher.class);

    /**
     * This field should contain the InformationObject that contains
     * all necessary information about the new update
     */
    private UpdateInformationObject informationObject;

    /**
     * Creates a basic update searcher object;
     *
     * <p>
     * When this constructor called the object automatically downloads
     * the information about the new update from the internet.
     */
    public UpdateSearcher() {
        try {
            informationObject = UpdateInformationLoader.load();
        } catch (IOException e) {
            Notification.create()
                    .level(NotificationLevel.ERROR)
                    .title("update.searcher.title")
                    .msg("update.searcher.error")
                    .cause(e)
                    .show();

            LOGGER.error("Couldn't download the information about the update", e);
        }
    }

    /**
     * Decides that there is new update available and notifies the user about it.
     *
     * <p>
     * The update-information is already downloaded
     * when the constructor called
     * This method decides that there is new update and notifies the user about it in that case.
     */
    public void search() {
        if (this.informationObject != null) {
            if (isCurrentVersionOld())
                Notification.create()
                        .level(NotificationLevel.INFO)
                        .title("update.searcher.title")
                        .msg("update.searcher.available")
                        .eventHandler(event -> new UpdateDisplayer().display(informationObject))
                        .show();
        }
    }

    /**
     * Queries the current version of the running application and compares to the new update's version.
     *
     * @return <code>true</code> if the current version of the application is old (so it's need to be updated)
     * or <code>false</code> otherwise
     */
    private boolean isCurrentVersionOld() {
        String currentVersion = BUILD_INFO.getVersion();
        String nextVersion = informationObject.getVersion();

        currentVersion = currentVersion.replace(".", "");
        nextVersion = nextVersion.replace(".", "");

        return Integer.parseInt(currentVersion) < Integer.parseInt(nextVersion);
    }

}
