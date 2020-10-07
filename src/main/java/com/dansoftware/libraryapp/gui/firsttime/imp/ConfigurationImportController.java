package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.PreferencesImporter;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationImportController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImportController.class);

    private final Context context;
    private final Preferences preferences;

    @FXML
    private TextField customLocationInput;

    public ConfigurationImportController(@NotNull Context context, @NotNull Preferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    @FXML
    private void importFromCustomLocation(ActionEvent event) {
        try {
            FileChooser fileChooser = createFileChooser();
            File file = fileChooser.showOpenDialog(WindowUtils.getWindowOf(((Node) event.getSource())));
            PreferencesImporter preferencesImporter = new PreferencesImporter(preferences);
            preferencesImporter.importFromZip(file, preferences);
        } catch (IOException e) {
            logger.error("Failed to import configurations", e);
            context.showErrorDialog(
                    I18N.getAlertMsg("configuration.import.failed.title"),
                    I18N.getAlertMsg("configuration.import.failed.msg"), e
            );
        }
    }

    @FXML
    private void close() {

    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*"));
        FileChooser.ExtensionFilter zipFiles = new FileChooser.ExtensionFilter("Zip files", "*.zip");
        fileChooser.getExtensionFilters().add(zipFiles);
        fileChooser.setSelectedExtensionFilter(zipFiles);
        return fileChooser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
