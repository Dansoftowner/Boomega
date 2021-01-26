package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorActivity;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseOpener;
import com.dansoftware.libraryapp.gui.dbmanager.DatabaseManagerActivity;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.login.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.gui.util.UIUtils;
import com.dansoftware.libraryapp.i18n.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Controller for the whole login box that has the database chooser and other additional controls.
 *
 * @author Daniel Gyorffy
 */
public class FrameFormController
        implements Initializable, DatabaseTracker.Observer {

    private static final Logger logger = LoggerFactory.getLogger(FrameFormController.class);

    private static final int INTERNAL_FORM_INDEX = 2;

    @FXML
    private VBox rootForm;

    @FXML
    private ComboBox<DatabaseMeta> databaseChooser;

    @FXML
    private Button fileChooserBtn;

    @FXML
    private Button managerBtn;

    /**
     * Set for filtering possible duplicate elements
     */
    private final Set<DatabaseMeta> databaseMetaSet;

    private final Context context;
    private final Preferences preferences;
    private final LoginData loginData;
    private final DatabaseTracker databaseTracker;
    private final DatabaseLoginListener databaseLoginListener;

    private Node internalForm;

    public FrameFormController(@NotNull Context context,
                               @NotNull Preferences preferences,
                               @NotNull LoginData loginData,
                               @NotNull DatabaseTracker databaseTracker,
                               @NotNull DatabaseLoginListener databaseLoginListener) {
        this.context = Objects.requireNonNull(context, "The context shouldn't be null!");
        this.preferences = Objects.requireNonNull(preferences);
        this.loginData = Objects.requireNonNull(loginData, "LoginData shouldn't be null");
        this.databaseTracker = Objects.requireNonNull(databaseTracker, "DatabaseTracker shouldn't be null");
        this.databaseLoginListener = Objects.requireNonNull(databaseLoginListener, "DatabaseLoginListener shouldn't be null");
        this.databaseMetaSet = new HashSet<>();
        this.databaseTracker.registerObserver(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setInternalFormBehaviour();
        setDefaults();
        fillForm(this.loginData);
    }

    private void fillForm(@NotNull LoginData loginData) {
        List<DatabaseMeta> lastDatabases = loginData.getSavedDatabases();
        this.databaseMetaSet.addAll(lastDatabases);
        this.databaseChooser.getItems().addAll(lastDatabases);
        DatabaseMeta selectedDatabase = loginData.getSelectedDatabase();
        if (selectedDatabase != null) {
            databaseChooser.getSelectionModel().select(selectedDatabase);
        }
    }

    private void setDefaults() {
        this.fileChooserBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN));
        this.managerBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        this.databaseChooser.setCellFactory(col -> new DatabaseChooserItem());
        this.databaseChooser.setButtonCell(new ComboBoxButtonCell());
    }

    private void setInternalFormBehaviour() {
        databaseChooser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loginData.setSelectedDatabase(newValue);
            if (newValue == null) this.rootForm.getChildren().remove(INTERNAL_FORM_INDEX);
            else if (oldValue == null) this.rootForm.getChildren().add(INTERNAL_FORM_INDEX, loadInternalForm());
        });
    }

    private Node loadInternalForm() {
        if (internalForm == null) {
            var fxmlController = new InternalFormController(context, preferences, databaseTracker, loginData, databaseLoginListener,
                    () -> databaseChooser.getSelectionModel().getSelectedItem());
            var fxmlLoader = new ImprovedFXMLLoader(fxmlController, getClass().getResource("InternalForm.fxml"), I18N.getLoginViewValues());
            internalForm = fxmlLoader.load();
        }

        return internalForm;
    }

    public ObservableStringValue titleProperty() {
        return this.databaseChooser.valueProperty().asString();
    }

    @Override
    public void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(() -> {
            UIUtils.refreshComboBox(this.databaseChooser);
            if (databaseMeta.equals(databaseChooser.getSelectionModel().getSelectedItem())) {
                databaseChooser.getSelectionModel().select(null);
            }
        });
    }

    @Override
    public void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(() -> UIUtils.refreshComboBox(this.databaseChooser));
    }

    @Override
    public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(() -> {
            if (databaseMetaSet.add(databaseMeta)) {
                logger.debug("Adding database {}", databaseMeta);
                this.databaseChooser.getItems().add(databaseMeta);
                this.loginData.getSavedDatabases().add(databaseMeta);
            } else logger.debug("Database already added {}", databaseMeta);
        });
    }

    @Override
    public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(() -> {
            if (databaseMetaSet.remove(databaseMeta)) {
                this.databaseChooser.getItems().remove(databaseMeta);
                this.loginData.getSavedDatabases().remove(databaseMeta);
            }
        });
    }

    /**
     * Creates a file-chooser dialog to import existing database files.
     */
    @FXML
    private void openFile() {
        var databaseOpener = new DatabaseOpener();
        List<DatabaseMeta> openedDatabases = databaseOpener.showMultipleOpenDialog(context.getContextWindow());
        openedDatabases.stream()
                .peek(databaseTracker::addDatabase)
                .reduce((first, second) -> second) //finding the last element
                .ifPresent(databaseChooser.getSelectionModel()::select);
    }

    @FXML
    private void callDataSourceAdder() {
        DatabaseCreatorActivity creatorActivity = new DatabaseCreatorActivity();
        Optional<DatabaseMeta> result = creatorActivity.show(this.databaseTracker, context.getContextWindow());
        result.ifPresent(this.databaseChooser.getSelectionModel()::select);
    }

    @FXML
    private void openDBManager() {
        DatabaseManagerActivity databaseManagerActivity = new DatabaseManagerActivity();
        databaseManagerActivity.show(this.databaseTracker, context.getContextWindow());
    }

    public LoginData getLoginData() {
        return loginData;
    }

    private class DatabaseChooserItem extends ListCell<DatabaseMeta> {

        private static final String NOT_EXISTS_CLASS = "state-indicator-file-not-exists";
        private static final String USED_CLASS = "state-indicator-used";

        DatabaseChooserItem() {
            setMaxWidth(650);
        }

        @Override
        protected void updateItem(DatabaseMeta item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.toString());
                File dbFile = item.getFile();
                if (!dbFile.exists() || dbFile.isDirectory()) {
                    var indicator = new FontAwesomeIconView(FontAwesomeIcon.WARNING);
                    indicator.getStyleClass().add(NOT_EXISTS_CLASS);
                    setGraphic(indicator);
                    setTooltip(new Tooltip(I18N.getGeneralValue("file.not.exists")));
                } else if (databaseTracker.isDatabaseUsed(item)) {
                    var indicator = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                    indicator.getStyleClass().add(USED_CLASS);
                    setGraphic(indicator);
                    setTooltip(new Tooltip(I18N.getGeneralValue("database.currently.used")));
                } else {
                    setGraphic(null);
                    setTooltip(null);
                }
            }
        }
    }

    private class ComboBoxButtonCell extends DatabaseChooserItem {
        @Override
        protected void updateItem(DatabaseMeta item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setText(I18N.getLoginViewValue("login.source.combo.promt"));
            }
        }
    }
}
