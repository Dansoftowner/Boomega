package com.dansoftware.libraryapp.gui.entry.login;

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

    @FXML
    private StackPane root;

    @FXML
    private ChoiceBox sourceChooser;

    @FXML
    private void addDataSource(ActionEvent event) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
