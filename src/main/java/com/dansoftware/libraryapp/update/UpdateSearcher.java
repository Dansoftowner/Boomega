package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.gui.update.UpdateDisplayer;
import com.dansoftware.libraryapp.log.GuiLog;
import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.main.Globals;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.dansoftware.libraryapp.util.Bundles.getCommonBundle;

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

    private static final Logger LOGGER = Logger.getLogger(UpdateSearcher.class.getName());

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
            informationObject = UpdateInformationObjectFactory.getInformation();
        } catch (IOException e) {
            LOGGER.log(new GuiLog(Level.SEVERE, e, "LibraryApp Updater", "update.cantsearch", null));
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
        Optional.ofNullable(this.informationObject)
                .ifPresent(informationObject -> {
                    if (isCurrentVersionOld())
                        Main.runAfterStart(() -> Notifications.create()
                                .title("LibraryApp Updater")
                                .text(getCommonBundle().getString("updatesearcher.available"))
                                .hideAfter(Duration.INDEFINITE)
                                .onAction(event -> new UpdateDisplayer().display(informationObject))
                                .showInformation()
                        );
                });
    }

    /**
     * Queries the current version of the running application and compares to the new update's version.
     *
     * @return <code>true</code> if the current version of the application is old (so it's need to be updated)
     *         or <code>false</code> otherwise
     */
    private boolean isCurrentVersionOld() {
        String currentVersion = Globals.APP_VERSION;
        String nextVersion = informationObject.getVersion();

        currentVersion = currentVersion.replace(".", "");
        nextVersion = nextVersion.replace(".", "");

        return Integer.parseInt(currentVersion) < Integer.parseInt(nextVersion);
    }

}
