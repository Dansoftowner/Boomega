package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.db.Account;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static com.dansoftware.libraryapp.locale.Bundles.getFXMLValues;

public class LoginForm extends StackPane implements Initializable {

    @FXML
    private ComboBox<Account> sourceChooser;

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
        this(new LoginData());
    }

    LoginForm(LoginData loginData) {
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

        this.loginData.setLoggedAccount(
                this.rememberBox.isSelected() ? account : null
        );

        if (Objects.nonNull(this.onLoginRequest)) {
            this.onLoginRequest.accept(account);
        }
    }

    @FXML
    private void callDataSourceAdder() {

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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
        //this.sourceChooser.setCellFactory(self -> new SourceChooserItem());
    }
}
