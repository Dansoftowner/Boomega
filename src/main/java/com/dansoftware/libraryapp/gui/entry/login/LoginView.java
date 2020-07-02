package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.AppConfig;
import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dansoftware.libraryapp.gui.workbench.LibraryAppWorkbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.exceptions.NitriteIOException;
import org.dizitart.no2.exceptions.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.Bundles.*;
import static com.dansoftware.libraryapp.main.Main.getAppConfig;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends LibraryAppWorkbench implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class);

    /* -------------------------------------------------
     * (View.fxml)
     * -------------------------------------------------
     */

    @FXML
    private StackPane root;

    @FXML
    private ComboBox<Account> sourceChooser;

    @FXML
    private VBox rootForm;

    /* -------------------------------------------------
     * LoginForm (Form.fxml)
     * -------------------------------------------------
     */

    @FXML
    private VBox loginForm;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private CheckBox rememberBox;

    /*
     * -------------------------------------------------
     */

    /**
     * Field that holds the selected database from the user.
     */
    private Database selectedDatabase;

    /**
     * ObservableValue that holds {@code true} if the sourceChooser choiceBox has elements;
     * {@code false} otherwise.
     */
    private BooleanBinding dataSourceSelected;

    private boolean loginDataNeedsSave;

    public LoginView() {
        initWorkbenchProperties();
        loadGui();
        loadLoginForm();
        addListeners();
    }

    public LoginView(LoginData loginData) {
        this();
        this.fillLoginForm(loginData);
    }

    /**
     *
     */
    private void initWorkbenchProperties() {
        //
        InfoWindow infoWindow = new InfoWindow(new InfoView());
        this.getToolbarControlsRight().add(new ToolbarItem(
                new MaterialDesignIconView(MaterialDesignIcon.INFORMATION),
                event -> {
                    if (infoWindow.isShowing()) {
                        infoWindow.close();
                    } else {
                        if (Objects.isNull(infoWindow.getOwner()))
                            infoWindow.initOwner(StageUtils.getStageOf(this));
                        infoWindow.show();
                    }
                }));
        this.getToolbarControlsLeft().add(new ToolbarItem("Libraryapp", new MaterialDesignIconView(MaterialDesignIcon.BOOK)));
    }

    /**
     * Loads the main-login-gui.
     */
    private void loadGui() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"), getFXMLValues());
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.getModules().add(new WorkbenchModule("Login", FontAwesomeIcon.SIGN_IN) {
            @Override
            public Node activate() {
                return root;
            }
        });

        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        Platform.runLater(() -> {
            StageUtils.getStageOf(this).setOnCloseRequest(event -> {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        LoginData loginData = LoginView.this.buildLoginData();

                        getAppConfig().set(AppConfig.Key.LOGIN_DATA, loginData);


                        return null;
                    }
                };

                this.showLoadingOverlay();
            });
        });
    }

    /**
     * Loads the login-form.
     */
    private void loadLoginForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Form.fxml"), getFXMLValues());
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addListeners() {
        //creating an observable-value that represents that the sourceChooser combobox has selected element
        this.dataSourceSelected = Bindings.isNotNull(sourceChooser.getSelectionModel().selectedItemProperty());
        this.dataSourceSelected.addListener((observable, oldValue, newValue) -> {
            //if there is selected element, we show the login form, otherwise we hide it
            if (newValue) this.rootForm.getChildren().add(1, this.loginForm);
            else this.rootForm.getChildren().remove(this.loginForm);
        });

        sourceChooser.getItems().addListener((ListChangeListener<Account>) change -> {
            this.loginDataNeedsSave = Boolean.TRUE;
        });

        this.rememberBox.selectedProperty().addListener(observable -> {
            this.loginDataNeedsSave = Boolean.TRUE;
        });
    }

    @FXML
    private void addDataSource(ActionEvent event) {
    }

    @FXML
    private void login(ActionEvent event) {
        try {
            // File file
            this.selectedDatabase = DatabaseFactory.getDatabase(NITRITE, new Account(
                    sourceChooser.getValue().getFile(),
                    StringUtils.trim(usernameInput.getText()),
                    StringUtils.trim(passwordInput.getText())
            ));
            StageUtils.getStageOf(this).close();
        } catch (SecurityException e) {
            String title = getNotificationMsg("login.auth.failed.security.title");
            String message = getNotificationMsg("login.auth.failed.security.msg");
            this.showErrorDialog(title, message, buttonType -> {
            });

            LOGGER.error("Failed to create/open database (invalid auth)", e);
        } catch (NitriteIOException e) {
            String title = getNotificationMsg("login.auth.failed.io.title");
            String message = getNotificationMsg("login.auth.failed.io.msg");
            this.showErrorDialog(title, message, e, buttonType -> {
            });

            LOGGER.error("Failed to create/open database (I/O)", e);
        }
    }

    private LoginData buildLoginData() {
        LoginData loginData = new LoginData(this.sourceChooser.getItems(), null);

        Account selectedAccount = this.sourceChooser.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedAccount) && this.rememberBox.isSelected()) {
            loginData.setLoggedAccount(new Account(
                    selectedAccount.getFile(),
                    usernameInput.getText(),
                    passwordInput.getText(),
                    selectedAccount.getDbName()
            ));
        }

        return loginData;
    }

    private void fillLoginForm(LoginData loginData) {
        this.sourceChooser.getItems().addAll(FXCollections.observableArrayList(loginData.getLastAccounts()));

        Account loggedAccount = loginData.getLoggedAccount();
        //loggedAccount != null means auto-login turned on
        if (Objects.nonNull(loggedAccount)) {
            this.sourceChooser.getSelectionModel().select(loggedAccount);
            this.usernameInput.setText(loggedAccount.getUsername());
            this.passwordInput.setText(loggedAccount.getPassword());
            this.rememberBox.setSelected(Boolean.TRUE);

            Platform.runLater(() -> this.login(null));
        }
    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rootForm.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
