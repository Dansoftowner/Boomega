package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class DataSourceAdder implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private Button dirOpenerBtn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField dirField;

    @FXML
    private TextField fullPathField;

    @FXML
    private CheckBox authCheckBox;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button saveBtn;

    private Account createdAccount;

    public DataSourceAdder() {
        loadGui();
    }

    private void loadGui() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DataSourceAdder.fxml"));
        fxmlLoader.setController(this);

        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Account> show(Window ownerWindow) {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
        stage.initModality(Modality.APPLICATION_MODAL);
        if (Objects.nonNull(ownerWindow)) {
            stage.initOwner(ownerWindow);

            //Applying stylesheets from the owner-window
            List<String> styleSheets = ownerWindow.getScene()
                    .getRoot()
                    .getStylesheets();

            stage.getScene().getStylesheets().addAll(styleSheets);
        }


        stage.showAndWait();

        return Optional.ofNullable(this.createdAccount);
    }

    @FXML
    private void save() {

    }

    @FXML
    private void openDirectory() {

    }

    private void setDefaults() {
        this.root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        this.dirOpenerBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN));
    }

    private void setBehaviour() {
        this.usernameField.disableProperty().bind(this.authCheckBox.selectedProperty());
        this.passwordField.disableProperty().bind(this.authCheckBox.selectedProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setBehaviour();
    }
}
