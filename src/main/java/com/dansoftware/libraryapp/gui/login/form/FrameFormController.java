package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorActivity;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseOpener;
import com.dansoftware.libraryapp.gui.dbmanager.DatabaseManagerActivity;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.gui.util.UIUtils;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

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

    private final LoginData loginData;

    private final DatabaseTracker databaseTracker;

    private final DatabaseLoginListener databaseLoginListener;

    private Node internalForm;

    public FrameFormController(@NotNull Context context,
                               @NotNull LoginData loginData,
                               @NotNull DatabaseTracker databaseTracker,
                               @NotNull DatabaseLoginListener databaseLoginListener) {
        this.context = Objects.requireNonNull(context, "The context shouldn't be null!");
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
        List<DatabaseMeta> lastDatabases = loginData.getLastDatabases();
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
            var fxmlController = new InternalFormController(
                    context,
                    loginData,
                    databaseLoginListener,
                    () -> databaseChooser.getSelectionModel().getSelectedItem()
            );

            var fxmlLoader = new ImprovedFXMLLoader(fxmlController, getClass().getResource("InternalForm.fxml"), I18N.getFXMLValues());
            internalForm = fxmlLoader.load();
        }

        return internalForm;
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
                this.databaseChooser.getItems().add(databaseMeta);
                this.loginData.getLastDatabases().add(databaseMeta);
            }
        });
    }

    @Override
    public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(() -> {
            if (databaseMetaSet.remove(databaseMeta)) {
                this.databaseChooser.getItems().remove(databaseMeta);
                this.loginData.getLastDatabases().remove(databaseMeta);
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
                if (databaseTracker.isDatabaseUsed(item)) {
                    var mark = new Circle(3) {{
                        getStyleClass().add("state-circle-used");
                    }};

                    setDisable(true);
                    setGraphic(mark);
                } else {
                    setGraphic(null);
                    setDisable(false);
                    setTooltip(null);
                }
            }
        }
    }
}
