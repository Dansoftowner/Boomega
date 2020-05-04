package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.main.Globals;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private Label versionLabel;

    @FXML
    private Label buildInfoLabel;

    @FXML
    private Label javaVMLabel;

    @FXML
    private Label javaVendorLabel;

    @FXML
    private Label javaVersionLabel;

    @FXML
    private Label javaFXVersionLabel;

    @FXML
    private void onCopy(ActionEvent event) {
        String toCopy =
                "Version: " + versionLabel.getText() + "\n" +
                "Build: " + buildInfoLabel.getText() + "\n" +
                "\n" +
                "Java VM: " + javaVMLabel.getText() + " By" + javaVendorLabel.getText() + "\n" +
                "Java version: " + javaVersionLabel.getText() + "\n" +
                "JavaFX version: " + javaFXVersionLabel.getText() + "\n";

        ClipboardContent content = new ClipboardContent();
        content.putString(toCopy);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }

    @FXML
    private void showDependencies(ActionEvent event) {

    }

    @FXML
    private void showDevelopers(ActionEvent event) {

    }

    @FXML
    private void showGithub(ActionEvent event) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionLabel.setText(Globals.BUILD_INFO.getVersion());
        buildInfoLabel.setText(Globals.BUILD_INFO.getBuildDate());
        javaVMLabel.setText(System.getProperty("java.vm.name"));
        javaVendorLabel.setText(System.getProperty("java.vendor"));
        javaVersionLabel.setText(System.getProperty("java.version"));
        javaFXVersionLabel.setText(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
    }
}
