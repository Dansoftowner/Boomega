package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
import com.dansoftware.libraryapp.gui.util.StageUtils;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.exceptions.NitriteIOException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.Bundles.getFXMLValues;
import static com.dansoftware.libraryapp.locale.Bundles.getNotificationMsg;

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the {@link Database} object.
 */
public class LoginView extends Workbench implements Initializable {

    /* -------------------------------------------------
     * (View.fxml)
     * -------------------------------------------------
     */

    @FXML
    private StackPane root;

    @FXML
    private ChoiceBox<String> sourceChooser;

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

    private Database selectedDatabase;

    private BooleanBinding sourceChooserEmpty;

    private Account initialAccount;

    public LoginView() {
        loadGui();
        loadLoginForm();
    }

    public LoginView(Account initialAccount) {
        this();
        this.initialAccount = initialAccount;
        this.fillLoginForm(initialAccount);
    }

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

        this.sourceChooserEmpty = Bindings.isEmpty(sourceChooser.getItems());
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
            this.selectedDatabase = DatabaseFactory.getDatabase(NITRITE, new Account(
                    sourceChooser.getValue(),
                    StringUtils.trim(usernameInput.getText()),
                    StringUtils.trim(passwordInput.getText())
            ));

            StageUtils.getStageOf(this).close();
        } catch (SecurityException e) {
            String title = getNotificationMsg("login.auth.failed.security.title");
            String message = getNotificationMsg("login.auth.failed.security.msg");
            this.showErrorDialog(title, message, e, buttonType -> {
            });
        } catch (NitriteIOException e) {
            String title = getNotificationMsg("login.auth.failed.io.title");
            String message = getNotificationMsg("login.auth.failed.io.msg");
            this.showErrorDialog(title, message, buttonType -> {
            });
        }
    }

    private void fillLoginForm(Account account) {
        this.sourceChooser.getItems().add(account.getFilePath());
        this.sourceChooser.getSelectionModel().select(account.getFilePath());
        this.usernameInput.textProperty().set(account.getUsername());
        this.passwordInput.textProperty().set(account.getPassword());
    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rootForm.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
