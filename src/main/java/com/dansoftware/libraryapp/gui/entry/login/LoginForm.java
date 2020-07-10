package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorView;
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorWindow;
import com.dansoftware.libraryapp.gui.entry.login.dbmanager.DBManagerView;
import com.dansoftware.libraryapp.gui.entry.login.dbmanager.DBManagerWindow;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.main.Globals;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
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
    private ComboBox<Account> sourceChooser;

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

    private ObservableValue<Boolean> dataSourceSelected;

    private final LoginData loginData;

    private Consumer<Account> onLoginRequest;

    LoginForm() {
        //calling with empty login-data
        this(new LoginData());
    }

    LoginForm(@NotNull LoginData loginData) {
        this.loginData = Objects.requireNonNull(loginData, "loginData mustn't be null");
        this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.loadGui();
        this.fillForm(this.loginData);
    }

    LoginForm(LoginData loginData, Consumer<Account> onLoginRequest) {
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
        this.sourceChooser.getItems().addAll(loginData.getLastAccounts());

        Account selectedAccount = loginData.getSelectedAccount();
        if (Objects.nonNull(selectedAccount)) {
            this.sourceChooser.getSelectionModel().select(selectedAccount);
        }

        Account loggedAccount = loginData.getLoggedAccount();
        if (Objects.nonNull(loggedAccount)) {
            this.sourceChooser.getSelectionModel().select(loggedAccount);
            this.usernameInput.setText(loggedAccount.getUsername());
            this.passwordInput.setText(loggedAccount.getPassword());
            this.rememberBox.setSelected(Boolean.TRUE);

            Platform.runLater(this::login);
        }
    }

    @FXML
    private void login() {

        Account account = new Account(
                sourceChooser.getValue().getFile(),
                StringUtils.trim(usernameInput.getText()),
                StringUtils.trim(passwordInput.getText())
        );

        if (this.rememberBox.isSelected()) {
            this.loginData.setLoggedAccount(account);
            LOGGER.debug("LoginData loggedAccount set to: " + account);
        } else {
            this.loginData.setLoggedAccount(null);
            LOGGER.debug("LoginData loggedAccount set to: null");
        }

        if (Objects.nonNull(this.onLoginRequest)) {
            this.onLoginRequest.accept(account);
        }
    }

    @FXML
    private void openFile() {
        FileChooser.ExtensionFilter dbExtension =
                new FileChooser.ExtensionFilter("LibraryApp database files", "*." + Globals.FILE_EXTENSION);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("All files", "*"), dbExtension);
        fileChooser.setSelectedExtensionFilter(dbExtension);

        File file = fileChooser.showOpenDialog(StageUtils.getWindowOf(this));
        if (Objects.nonNull(file)) {
            Account account = new Account(file, null, null, file.getName());
            this.sourceChooser.getItems().add(account);
            this.sourceChooser.getSelectionModel().select(account);
        }
    }

    @FXML
    private void callDataSourceAdder() {
        DatabaseCreatorView view = new DatabaseCreatorView();

        DatabaseCreatorWindow window = new DatabaseCreatorWindow(view, StageUtils.getStageOf(this));
        window.showAndWait();

        Optional<Account> result = view.getCreatedAccount();

        result.ifPresent(account -> {
            this.sourceChooser.getItems().add(account);
            this.sourceChooser.getSelectionModel().select(account);
        });
    }

    @FXML
    private void openDBManager() {
        DBManagerView view = new DBManagerView(this.sourceChooser.getItems());
        DBManagerWindow window = new DBManagerWindow(view, StageUtils.getStageOf(this));
        window.show();
    }

    public void setOnLoginRequest(Consumer<Account> onLoginRequest) {
        this.onLoginRequest = onLoginRequest;
    }

    public LoginData getLoginData() {
        return this.loginData;
    }

    private void addListeners() {
        this.sourceChooser.getItems().addListener((ListChangeListener<Account>) observable -> {
            this.loginData.setLastAccounts(this.sourceChooser.getItems());
        });

        this.getChildren().remove(this.loginForm);

        //creating an observable-value that represents that the sourceChooser combobox has selected element
        this.dataSourceSelected = Bindings.isNotNull(sourceChooser.getSelectionModel().selectedItemProperty());
        this.dataSourceSelected.addListener((observable, oldValue, newValue) -> {
            //if there is selected element, we show the login form, otherwise we hide it
            if (newValue)
                this.rootForm.getChildren().add(1, this.loginForm);
            else
                this.rootForm.getChildren().remove(this.loginForm);
        });

        this.sourceChooser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loginData.setSelectedAccount(newValue);
        });

        this.managerBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
    }

    private void setDefaults() {
        this.rootForm.getChildren().remove(this.loginForm);
        this.fileChooserBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
        setDefaults();
        //this.sourceChooser.setCellFactory(self -> new SourceChooserItem());
    }
}
