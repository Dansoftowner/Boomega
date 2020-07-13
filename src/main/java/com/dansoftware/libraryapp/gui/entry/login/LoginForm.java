package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.DBMeta;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorView;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorWindow;
import com.dansoftware.libraryapp.gui.entry.login.dbmanager.DBManagerView;
import com.dansoftware.libraryapp.gui.entry.login.dbmanager.DBManagerWindow;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.util.UniqueListWrapper;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import static com.dansoftware.libraryapp.locale.I18N.getFXMLValues;

/**
 * A LoginForm is gui-form that lets the user to sign in to
 * a database. It's usually wrapped in a {@link LoginView}
 * object.
 */
public class LoginForm extends StackPane implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginForm.class);

    @FXML
    private ComboBox<DBMeta> sourceChooser;

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

    /**
     * A 'wrapped' version of the {@link #sourceChooser} object's
     * {@link ComboBox#getItems()} observable-list that does not allow to put
     * duplicate elements.
     *
     * @see UniqueListWrapper
     */
    private final List<DBMeta> predicatedDBList;

    private final LoginData loginData;

    private ObservableValue<Boolean> dataSourceSelected;

    private Consumer<Account> onLoginRequest;

    LoginForm() {
        //calling with empty login-data
        this(new LoginData());
    }

    LoginForm(@NotNull LoginData loginData) {
        this.loginData = Objects.requireNonNull(loginData, "loginData mustn't be null");
        this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.loadGui();
        this.predicatedDBList = new UniqueListWrapper<>(sourceChooser.getItems());
        this.fillForm(this.loginData);
    }

    LoginForm(@NotNull LoginData loginData, @Nullable Consumer<Account> onLoginRequest) {
        this(loginData);
        this.onLoginRequest = onLoginRequest;
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
        this.predicatedDBList.addAll(loginData.getLastDatabases());

        int selectedDB = loginData.getSelectedDBIndex();
        if (selectedDB >= 0) {
            sourceChooser.getSelectionModel().select(selectedDB);
        }

        DBMeta loggedDB = loginData.getLoggedDB();
        if (Objects.nonNull(loggedDB)) {
            sourceChooser.getSelectionModel().select(loggedDB);
            rememberBox.setSelected(Boolean.TRUE);

            LoginData.Credentials credentials = loginData.getLoggedDBCredentials();
            if (Objects.nonNull(credentials)) {
                usernameInput.setText(credentials.getUsername());
                passwordInput.setText(credentials.getPassword());
            }

            Platform.runLater(this::login);
        } else {
            loginData.setLoggedDBCredentials(null);
        }
    }

    @FXML
    private void login() {
        File dbFile = sourceChooser.getValue().getFile();
        String username = StringUtils.trim(usernameInput.getText());
        String password = StringUtils.trim(passwordInput.getText());

        //creating an Account object that holds the credentials (username/password)
        Account account = new Account(dbFile, username, password);

        if (rememberBox.isSelected()) {
            loginData.setLoggedDBIndex(sourceChooser.getSelectionModel().getSelectedIndex());
            loginData.setLoggedDBCredentials(new LoginData.Credentials(username, password));
            LOGGER.debug("LoginData loggedAccount set to: " + account);
        } else {
            loginData.setLoggedDBIndex(-1);
            loginData.setLoggedDBCredentials(null);
            LOGGER.debug("LoginData loggedAccount set to: null");
        }

        if (Objects.nonNull(this.onLoginRequest)) {
            this.onLoginRequest.accept(account);
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

            DBMeta lastElement;
            do {
                lastElement = new DBMeta(iterator.next());
                try {
                    predicatedDBList.add(lastElement);
                } catch (UniqueListWrapper.NonUniqueElementException e) {
                    LOGGER.error("Duplicate element has been filtered", e);
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

        Optional<DBMeta> result = view.getCreatedAccount();

        result.ifPresent(db -> {
            try {
                this.predicatedDBList.add(db);
                this.sourceChooser.getSelectionModel().select(db);
            } catch (UniqueListWrapper.NonUniqueElementException e) {
                LOGGER.error("Duplicate element has been filtered", e);
            }
        });
    }

    @FXML
    private void openDBManager() {
        DBManagerView view = new DBManagerView(this.predicatedDBList);
        DBManagerWindow window = new DBManagerWindow(view, WindowUtils.getStageOf(this));
        window.show();
    }

    public void setOnLoginRequest(Consumer<Account> onLoginRequest) {
        this.onLoginRequest = onLoginRequest;
    }

    public LoginData getLoginData() {
        return this.loginData;
    }

    private void addListeners() {
        this.sourceChooser.getItems().addListener((ListChangeListener<DBMeta>) observable -> {
            this.loginData.setLastDatabases(this.sourceChooser.getItems());
        });

        //creating an observable-value that represents that the sourceChooser combobox has selected element
        this.dataSourceSelected = Bindings.isNotNull(sourceChooser.getSelectionModel().selectedItemProperty());
        this.dataSourceSelected.addListener((observable, oldValue, newValue) -> {
            //if there is selected element, we show the login form, otherwise we hide it
            if (newValue)
                this.rootForm.getChildren().add(1, this.loginForm);
            else
                this.rootForm.getChildren().remove(this.loginForm);
        });

        this.sourceChooser.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    loginData.setSelectedDbIndex((Integer) newValue);
                });

        this.managerBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
    }

    private void setDefaults() {
        this.rootForm.getChildren().remove(this.loginForm);
        this.fileChooserBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        addListeners();
        //this.sourceChooser.setCellFactory(self -> new SourceChooserItem());

    }
}
