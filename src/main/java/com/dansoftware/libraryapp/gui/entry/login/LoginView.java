package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseFactory;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.dizitart.no2.exceptions.NitriteIOException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.db.DatabaseFactory.NITRITE;
import static com.dansoftware.libraryapp.locale.Bundles.getFXMLValues;
import static java.util.Objects.isNull;

public class LoginView extends Workbench implements Initializable {

    @FXML
    private StackPane root;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private ChoiceBox<String> sourceChooser;

    @FXML
    private VBox loginForm;

    @FXML
    private VBox rootForm;

    private Database selectedDatabase;

    private BooleanBinding sourceChooserEmpty;

    private Account account;

    public LoginView() {
        initGui();
    }

    public LoginView(Account account) {
        this();
        this.account = account;
    }

    private void initGui() {
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
    }

    @FXML
    private void addDataSource(ActionEvent event) {
    }

    @FXML
    private void login(ActionEvent event) {
        try {
            this.selectedDatabase = DatabaseFactory.getDatabase(NITRITE, new Account(
                    sourceChooser.getValue(),
                    usernameInput.getText(),
                    passwordInput.getText()
            ));
        } catch (SecurityException e) {

        } catch (NitriteIOException e) {

        }

        //getStage().close();
    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    /**
     * Loads the login-form.
     * It's called by the {@link LoginView#initialize(URL, ResourceBundle)} method.
     */
    private void loadLoginForm() {
        if (isNull(this.loginForm)) {
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
                else this.rootForm.getChildren().add(1, this.rootForm);
            });

            root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadLoginForm();
    }
}
