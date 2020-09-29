package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.info.dependency.DependencyViewerActivity;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class InformationView extends SimpleHeaderView<Node>
        implements Initializable {

    private static final double MAX_WIDTH = 600;
    private static final double MAX_HEIGHT = 300;

    private final Context context;

    @FXML
    private Label versionLabel;

    @FXML
    private Label javaVMLabel;

    @FXML
    private Label javaVendorLabel;

    @FXML
    private Label javaVersionLabel;

    @FXML
    private Label javaFXVersionLabel;

    @FXML
    private Button gitHubBtn;

    @FXML
    private Button showDepsBtn;

    @FXML
    private TextField logsLocationField;


    public InformationView(@NotNull Context context) {
        super("LibraryApp Info", new MaterialDesignIconView(MaterialDesignIcon.INFORMATION));
        this.context = context;
        this.setMaxWidth(MAX_WIDTH);
        this.setMaxHeight(MAX_HEIGHT);
        this.createToolbarControls();
        this.loadContent();
    }

    private void createToolbarControls() {
        var copyItem = new ToolbarItem(
                StringUtils.EMPTY,
                new MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY),
                this::onCopy
        );
        copyItem.setTooltip(new Tooltip(I18N.getFXMLValues().getString("info.copy")));
        this.getToolbarControlsRight().add(copyItem);
    }

    private void loadContent() {
        var fxmlLoader = new ImprovedFXMLLoader(this, getClass().getResource("View.fxml"), I18N.getFXMLValues());
        this.setContent(fxmlLoader.load());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setData();
    }

    private void setDefaults() {
        gitHubBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.GITHUB_BOX));
        showDepsBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.LANGUAGE_JAVASCRIPT));
    }

    private void setData() {
        versionLabel.setText(System.getProperty("libraryapp.version"));
        javaVMLabel.setText(System.getProperty("java.vm.name"));
        javaVendorLabel.setText(System.getProperty("java.vendor"));
        javaVersionLabel.setText(System.getProperty("java.version"));
        javaFXVersionLabel.setText(System.getProperty("javafx.version"));
        logsLocationField.setText(System.getProperty("log.file.path.full"));
    }

    private void onCopy(Event event) {
        try {
            String resourcePath = "/com/dansoftware/libraryapp/gui/info/toCopy.txt";
            String resource = IOUtils.resourceToString(resourcePath, StandardCharsets.UTF_8);

            String toCopy = MessageFormat.format(resource,
                    System.getProperty("libraryapp.version"),
                    System.getProperty("libraryapp.build.info"),
                    System.getProperty("os.name"),
                    System.getProperty("os.version"),
                    System.getProperty("java.vm.name"),
                    System.getProperty("java.vendor"),
                    System.getProperty("java.version"),
                    System.getProperty("javafx.version")
            );

            ClipboardContent content = new ClipboardContent();
            content.putString(toCopy);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);
        } catch (IOException e) {
//            showErrorDialog()
        }
    }

    @FXML
    private void showSoftwareDevelopers(ActionEvent event) {

    }

    @FXML
    private void showUsedDependencies(ActionEvent event) {
        new DependencyViewerActivity(this.context).show();
    }
}
