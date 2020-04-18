package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfiguration;
import com.dansoftware.libraryapp.gui.update.UpdateDisplayer;
import com.dansoftware.libraryapp.log.GuiLog;
import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.util.ApplicationMeta;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.dansoftware.libraryapp.util.Bundles.getCommonBundle;
import static java.lang.Boolean.*;

/**
 * This class is responsible for deciding that there is new update or not.
 * If there is, displays it to the user by the {@link UpdateDisplayer}
 */
public class UpdateSearcher {

    private static final Logger LOGGER = Logger.getLogger(UpdateSearcher.class.getName());

    /**
     * This field should contain the InformationObject that contains
     * all necessary information about the new update
     */
    private UpdateInformationObject informationObject;

    /**
     * Creates a basic update searcher object and reads the information of the new update
     * from the web if the automatic update-searching is turned on by the user.
     * This constructor calls the {@link UpdateSearcher#UpdateSearcher(boolean)} with
     * <code>true</code> value
     *
     * @see UpdateSearcher#UpdateSearcher(boolean)
     */
    public UpdateSearcher() {
        this(TRUE);
    }

    /**
     * Creates a basic update searcher object and reads the information of the new update
     * from the web if the automatic update-searching is turned on by the user EXCEPT the
     * {@link Boolean#FALSE} value passed as parameter, because in that case we don't care
     * about that the automatic-update-searching is turned on or not.
     *
     * @param considerateConfiguration defines that we care about the automatic-update-searching or not.
     */
    public UpdateSearcher(boolean considerateConfiguration) {
        if (considerateConfiguration)
            if (!automaticSearchingAllowed())
                return;

        try {
            informationObject = UpdateInformationObjectFactory.getInformation();
        } catch (IOException e) {
            LOGGER.log(new GuiLog(Level.SEVERE, e, "LibraryApp Updater", "update.cantsearch"));
        }
    }

    /**
     * This method decides that there is new update and notifies the user about it in that case.
     */
    public void search() {
        Optional.ofNullable(this.informationObject)
                .ifPresent(informationObject -> {
                    if (isCurrentVersionOld()) {
                        Main.runAfterStart(() -> Notifications.create()
                                .title("LibraryApp Updater")
                                .text(getCommonBundle().getString("updatesearcher.available"))
                                .hideAfter(Duration.INDEFINITE)
                                .onAction(event -> new UpdateDisplayer().display(informationObject))
                                .showInformation());
                    }
                });

    }

    /**
     * @return <code>true</code> if the automatic update search is allowed and <code>false</code> otherwise.
     */
    private boolean automaticSearchingAllowed() {
        return Boolean.parseBoolean(ConfigurationHandler.getInstance()
                .getConfiguration(PredefinedConfiguration.SEARCH_FOR_UPDATES_AT_START.getKey()));
    }

    /**
     * @return <code>true</code> if the current version of the application is old (so it's needed to be updated)
     *         and <code>false</code> otherwise
     */
    private boolean isCurrentVersionOld() {
        String currentVersion = ApplicationMeta.APP_VERSION;
        String nextVersion = informationObject.getVersion();

        currentVersion = currentVersion.replace(".", "");
        nextVersion = nextVersion.replace(".", "");

        return Integer.parseInt(currentVersion) < Integer.parseInt(nextVersion);
    }

}
