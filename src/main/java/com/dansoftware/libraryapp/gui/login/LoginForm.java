package com.dansoftware.libraryapp.gui.login;

import com.dansoftware.libraryapp.db.*;
import com.dansoftware.libraryapp.db.processor.LoginProcessor;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorView;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorWindow;
import com.dansoftware.libraryapp.gui.dbmanager.DBManagerView;
import com.dansoftware.libraryapp.gui.dbmanager.DBManagerWindow;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.util.UniqueList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.dansoftware.libraryapp.locale.I18N.getFXMLValues;

/**
 * A LoginForm is gui-form that lets the user to sign in to
 * a database. It's usually wrapped in a {@link LoginView}
 * object.
 */
public class LoginForm extends StackPane implements Initializable, DatabaseTracker.Observer {

    private static final Logger logger = LoggerFactory.getLogger(LoginForm.class);

    private static final String STYLE_CLASS = "login-form";

    private static final int LOGIN_BOX_INDEX = 2;

    @FXML
    private ComboBox<DatabaseMeta> sourceChooser;

    @FXML
    private Button fileChooserBtn;

    @FXML
    private Button managerBtn;

    @FXML
    private VBox rootForm;

    @FXML
    private Region loginForm;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private CheckBox rememberBox;

    private ObjectProperty<Database> createdDatabase;

    /**
     * A 'wrapped' version of the {@link #sourceChooser} object's
     * {@link ComboBox#getItems()} observable-list that does not allow to put
     * duplicate elements.
     *
     * @see UniqueList
     */
    private final List<DatabaseMeta> predicatedDBList;

    private final DatabaseTracker databaseTracker;

    private final LoginData loginData;

    private final LoginView loginView;

    private ObservableValue<Boolean> dataSourceSelected;

    LoginForm(@NotNull LoginView loginView, @NotNull LoginData loginData, @NotNull DatabaseTracker tracker) {
        this.loginData = Objects.requireNonNull(loginData, "loginData mustn't be null");
        this.loginView = Objects.requireNonNull(loginView, "The LoginView shouldn't be null");
        this.databaseTracker = Objects.requireNonNull(tracker, "The DatabaseTracker shouldn't be null");
        this.createdDatabase = new SimpleObjectProperty<>(this, "createdDatabase");
        this.getStyleClass().add(STYLE_CLASS);
        this.loadGui();
        this.predicatedDBList = new UniqueList<>(sourceChooser.getItems());
        this.fillForm(this.loginData);

        this.databaseTracker.registerObserver(this);
    }


    private void loadGui() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"), getFXMLValues());
        fxmlLoader.setController(this);

        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillForm(LoginData loginData) {
        try {
            this.predicatedDBList.addAll(loginData.getLastDatabases());
        } catch (UniqueList.DuplicateElementException e) {
            logger.error("Duplicate element ", e);
        }

        DatabaseMeta selectedDatabase = loginData.getSelectedDatabase();
        if (selectedDatabase != null) {
            //logger.debug("Selecting the database read from loginData...");
            sourceChooser.getSelectionModel().select(selectedDatabase);
        }

        DatabaseMeta autoLoginDatabase = loginData.getAutoLoginDatabase();
        if (autoLoginDatabase != null) {
            logger.debug("Auto-login database detected");
            sourceChooser.getSelectionModel().select(autoLoginDatabase);
            rememberBox.setSelected(Boolean.TRUE);

            Credentials credentials = loginData.getAutoLoginCredentials();
            if (Objects.nonNull(credentials)) {
                usernameInput.setText(credentials.getUsername());
                passwordInput.setText(credentials.getPassword());
            }
        } /*else {
            loginData.setAutoLoginCredentials(null);
        }*/
    }

    @FXML
    private void login() {
        DatabaseMeta dbMeta = sourceChooser.getSelectionModel().getSelectedItem();
        File dbFile = dbMeta.getFile();
        String username = StringUtils.trim(usernameInput.getText());
        String password = StringUtils.trim(passwordInput.getText());

        //creating an object that holds the credentials (username/password)
        Credentials credentials = new Credentials(username, password);

        if (rememberBox.isSelected()) {
            loginData.setAutoLoginDatabase(dbMeta);
            loginData.setAutoLoginCredentials(credentials);
        } else {
            loginData.setAutoLoginDatabase(null);
            loginData.setAutoLoginCredentials(null);
        }

        this.createdDatabase.set(LoginProcessor.of(NitriteDatabase.factory())
                .onFailed((title, message, t) -> {
                    this.loginView.showErrorDialog(title, message, ((Exception) t), buttonType -> {
                    });

                    logger.error("Failed to create/open the database", t);
                }).process(dbMeta, credentials));

        if (this.createdDatabase.get() != null) {
            //creating the database was successful
            logger.debug("Signing in was successful; closing the LoginWindow");
            //starting a thread that saves the login-data
            new Thread(new LoginDataSaver(this.getLoginData())).start();

            logger.debug("The window is '{}'", WindowUtils.getStageOf(this) );

            WindowUtils.getStageOf(this).close();
        }
    }

    /**
     * Creates a file-chooser dialog to import existing database files.
     */
    @FXML
    private void openFile() {
        //file extension filter for the libraryapp-database files
        FileChooser.ExtensionFilter dbExtension =
                new FileChooser.ExtensionFilter("LibraryApp database files", "*." + Globals.FILE_EXTENSION);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("All files", "*"), dbExtension);
        fileChooser.setSelectedExtensionFilter(dbExtension);

        //we allow to select multiple files
        List<File> files = fileChooser.showOpenMultipleDialog(WindowUtils.getWindowOf(this));
        if (CollectionUtils.isNotEmpty(files)) {
            Iterator<File> iterator = files.iterator();

            //we save the size of the database-list before adding new elements
            int lastSize = predicatedDBList.size();

            DatabaseMeta lastElement;
            do {
                lastElement = new DatabaseMeta(iterator.next());
                try {
                    predicatedDBList.add(lastElement);
                } catch (UniqueList.DuplicateElementException e) {
                    logger.error("Duplicate element has been filtered", e);
                }
            } while (iterator.hasNext());

            //if we added new elements, we select the lastElement
            if (lastSize < predicatedDBList.size()) {
                sourceChooser.getSelectionModel().select(lastElement);
            }
        }
    }

    @FXML
    private void callDataSourceAdder() {
        DatabaseCreatorView view = new DatabaseCreatorView();

        DatabaseCreatorWindow window = new DatabaseCreatorWindow(view, WindowUtils.getStageOf(this));
        window.showAndWait();

        Optional<DatabaseMeta> result = view.getCreatedAccount();

        result.ifPresent(db -> {
            try {
                this.predicatedDBList.add(db);
                this.sourceChooser.getSelectionModel().select(db);
            } catch (UniqueList.DuplicateElementException e) {
                logger.error("Duplicate element has been filtered", e);
            }
        });
    }

    @FXML
    private void openDBManager() {
        DBManagerView view = new DBManagerView(this.predicatedDBList, this.databaseTracker);
        DBManagerWindow window = new DBManagerWindow(view, WindowUtils.getStageOf(this));
        window.show();
    }

    public LoginData getLoginData() {
        this.loginData.setLastDatabases(sourceChooser.getItems());
        this.loginData.setSelectedDatabase(sourceChooser.getSelectionModel().getSelectedItem());

        return this.loginData;
    }

    public Database getCreatedDatabase() {
        return createdDatabase.get();
    }

    private void addListeners() {
  /*      this.sourceChooser.getItems().addListener((ListChangeListener<DatabaseMeta>) observable -> {
            this.loginData.setLastDatabases(this.sourceChooser.getItems());
        });*/

        //creating an observable-value that represents that the sourceChooser combobox has selected element
        this.dataSourceSelected = Bindings.isNotNull(sourceChooser.getSelectionModel().selectedItemProperty());
        this.dataSourceSelected.addListener((observable, oldValue, newValue) -> {
            //if there is selected element, we show the login form, otherwise we hide it
            if (newValue)
                this.rootForm.getChildren().add(LOGIN_BOX_INDEX, this.loginForm);
            else
                this.rootForm.getChildren().remove(LOGIN_BOX_INDEX);
        });
/*
        this.sourceChooser.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    loginData.setSelectedDatabase(newValue);
                });
*/
    }

    private void setDefaults() {
        this.rootForm.getChildren().remove(this.loginForm);
        this.fileChooserBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN));
        this.managerBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        this.sourceChooser.setCellFactory(col -> new SourceChooserItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        addListeners();
        //this.sourceChooser.setCellFactory(self -> new SourceChooserItem());

    }

    private <T> void refreshComboBox(ComboBox<T> comboBox) {
        ObservableList<T> items = comboBox.getItems();
        T selected = comboBox.getSelectionModel().getSelectedItem();
        comboBox.setItems(null);
        comboBox.setItems(items);
        comboBox.getSelectionModel().select(selected);
    }

    @Override
    public void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
        Platform.runLater(() -> {
            refreshComboBox(this.sourceChooser);
            if (databaseMeta.equals(sourceChooser.getSelectionModel().getSelectedItem())) {
                sourceChooser.getSelectionModel().select(null);
            }
        });
    }

    @Override
    public void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
        Platform.runLater(() -> refreshComboBox(this.sourceChooser));
    }

    @Override
    public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
        Platform.runLater(() -> {
            try {
                this.predicatedDBList.add(databaseMeta);
            } catch (UniqueList.DuplicateElementException e) {
                logger.error("Duplicate element added");
            }
        });
    }

    @Override
    public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        Platform.runLater(() -> this.predicatedDBList.remove(databaseMeta));
    }

    private class SourceChooserItem extends ListCell<DatabaseMeta> {
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
