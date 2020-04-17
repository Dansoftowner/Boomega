package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.gui.update.UpdateDisplayer;
import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.util.ApplicationMeta;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.Optional;

import static com.dansoftware.libraryapp.util.Bundles.getCommonBundle;

public class UpdateSearcher {

    private UpdateInformationObject informationObject;

    public UpdateSearcher() {
        informationObject = UpdateInformationObjectFactory.getInformation();
    }

    public void search() {
        Optional.ofNullable(this.informationObject)
                .ifPresent(informationObject -> {
                    if (isCurrentVersionOld()) {
                        Main.runAfterStart(() -> Notifications.create()
                                .title("Update checker")
                                .text(getCommonBundle().getString("updatesearcher.available"))
                                .hideAfter(Duration.INDEFINITE)
                                .onAction(event -> new UpdateDisplayer().display(informationObject))
                                .showInformation());
                    }
                });
    }

    private boolean isThereIsNeedToSearch() {
        return (Boolean) null;
    }

    private boolean isCurrentVersionOld() {
        String currentVersion = ApplicationMeta.APP_VERSION;
        String nextVersion = informationObject.getVersion();

        currentVersion = currentVersion.replace(".", "");
        nextVersion = nextVersion.replace(".", "");

        return Integer.parseInt(currentVersion) < Integer.parseInt(nextVersion);
    }

}
