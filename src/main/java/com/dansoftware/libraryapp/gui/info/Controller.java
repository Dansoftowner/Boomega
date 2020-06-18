package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.main.Globals;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
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
    private TextField logsLocationLabel;

    @FXML
    private void onCopy(ActionEvent event) throws IOException {
        String resourcePath = "/com/dansoftware/libraryapp/gui/info/toCopy.txt";
        String resource = IOUtils.resourceToString(resourcePath, StandardCharsets.UTF_8);
        String toCopy = MessageFormat.format(resource,
                Globals.VERSION_INFO,
                Globals.VERSION_INFO.getBuildInfo(),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("java.vm.name"),
                System.getProperty("java.vendor"),
                System.getProperty("java.version"),
                com.sun.javafx.runtime.VersionInfo.getRuntimeVersion()
        );

        ClipboardContent content = new ClipboardContent();
        content.putString(toCopy);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }

    @FXML
    private void showDependencies(ActionEvent event) {
        System.out.println(((Button) event.getSource()).getText());
    }

    @FXML
    private void showDevelopers(ActionEvent event) {
        System.out.println(((Button) event.getSource()).getText());
    }

    @FXML
    private void showGithub(ActionEvent event) {
        System.out.println(((Button) event.getSource()).getTooltip().getText());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionLabel.setText(Globals.VERSION_INFO.getVersion());
        buildInfoLabel.setText(Globals.VERSION_INFO.getBuildInfo());
        javaVMLabel.setText(System.getProperty("java.vm.name"));
        javaVendorLabel.setText(System.getProperty("java.vendor"));
        javaVersionLabel.setText(System.getProperty("java.version"));
        javaFXVersionLabel.setText(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        logsLocationLabel.setText(System.getProperty("log.file.path.full"));
    }
}
