package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.info.InfoView;
import com.dansoftware.libraryapp.gui.info.InfoWindow;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;
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

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends Workbench implements Initializable {

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
     * ObservableValue that holds {@code true} if the sourceChooser choiceBox has no elements;
     * {@code false} otherwise.
     */
    private BooleanBinding sourceChooserEmpty;

    private LoginData loginData;

    public LoginView() {
        initWorkbenchProperties();
        loadGui();
        loadLoginForm();
    }

    public LoginView(LoginData loginData) {
        this();
        this.loginData = loginData;
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

        Parent root;
        try {
            root = fxmlLoader.load();
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

        this.sourceChooserEmpty = Bindings.isNull(sourceChooser.getSelectionModel().selectedItemProperty());
        this.sourceChooserEmpty.addListener((observable, oldValue, newValue) -> {
            if (newValue) this.rootForm.getChildren().remove(this.loginForm);
            else this.rootForm.getChildren().add(1, this.loginForm);
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

    private void fillLoginForm(LoginData loginData) {
        this.sourceChooser.getItems().addAll(FXCollections.observableArrayList(loginData.getLastAccounts()));

        Account loggedAccount = loginData.getLoggedAccount();
        //loggedAccount != null means auto-login turned on
        if (Objects.nonNull(loggedAccount)) {
            this.sourceChooser.getSelectionModel().select(loggedAccount);
            this.usernameInput.setText(loggedAccount.getUsername());
            this.passwordInput.setText(loggedAccount.getPassword());
            this.rememberBox.setSelected(Boolean.TRUE);
            this.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> sceneProperty, Scene oldScene, Scene newScene) {
                    //create a reference of the scene-listener itself
                    ChangeListener<Scene> sceneListener = this;

                    //listening a window
                    newScene.windowProperty().addListener(new ChangeListener<Window>() {
                        @Override
                        public void changed(ObservableValue<? extends Window> windowProperty,
                                            Window oldWindow,
                                            Window newWindow) {
                            //when the window is shown, we try to login automatically
                            newWindow.setOnShown(event -> {
                                LoginView.this.login(null);

                                //remove all listeners to let JVM release memory
                                windowProperty.removeListener(this);
                                sceneProperty.removeListener(sceneListener);
                            });
                        }
                    });
                }
            });
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
