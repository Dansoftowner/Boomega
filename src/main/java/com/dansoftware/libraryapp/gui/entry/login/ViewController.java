package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.StackPane;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

    private Database selectedDatabase;

    @FXML
    private StackPane root;

    @FXML
    private ChoiceBox<String> sourceChooser;

    @FXML
    private void addDataSource(ActionEvent event) {

    }

    public Database getSelectedDatabase() {
        return selectedDatabase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
