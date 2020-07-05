package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        return Optional.empty();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
