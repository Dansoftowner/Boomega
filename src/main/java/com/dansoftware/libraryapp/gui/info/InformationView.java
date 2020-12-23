package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.info.dependency.DependencyViewerActivity;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.locale.LanguageTranslator;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.dansoftware.libraryapp.gui.info.ApplicationInfoCopyKt.getApplicationInfoCopy;

/**
 * An InformationView is the actual GUI element that shows the
 * information of the application.
 *
 * @author Daniel Gyorffy
 */
public class InformationView extends SimpleHeaderView<Node>
        implements Initializable {

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
    private Label javaHomeLabel;

    @FXML
    private Label javaFXVersionLabel;

    @FXML
    private Label langLabel;

    @FXML
    private Label langTranslatorLabel;

    @FXML
    private Button gitHubBtn;

    @FXML
    private Button showDepsBtn;

    @FXML
    private TextField logsLocationField;


    public InformationView(@NotNull Context context) {
        super("LibraryApp Info", new MaterialDesignIconView(MaterialDesignIcon.INFORMATION));
        this.context = context;
        this.setEffect(new DropShadow());
        this.createToolbarControls();
        this.loadContent();
    }

    @FXML
    private void showUsedDependencies(ActionEvent event) {
        new DependencyViewerActivity(this.context).show();
    }

    private void createToolbarControls() {
        var copyItem = new ToolbarItem(
                StringUtils.EMPTY,
                new MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY),
                this::onCopy
        );
        copyItem.setTooltip(new Tooltip(I18N.getInfoViewValues().getString("info.copy")));
        this.getToolbarControlsRight().add(copyItem);
    }

    private void loadContent() {
        var fxmlLoader = new ImprovedFXMLLoader(this, getClass().getResource("View.fxml"), I18N.getInfoViewValues());
        this.setContent(fxmlLoader.load());
    }

    private void onCopy(Event event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(getApplicationInfoCopy());
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }

    private void setData() {
        versionLabel.setText(System.getProperty("libraryapp.version"));
        javaVMLabel.setText(System.getProperty("java.vm.name"));
        javaVendorLabel.setText(System.getProperty("java.vendor"));
        javaHomeLabel.setText(System.getProperty("java.home"));
        javaVersionLabel.setText(System.getProperty("java.version"));
        javaFXVersionLabel.setText(System.getProperty("javafx.version"));
        logsLocationField.setText(System.getProperty("log.file.path.full"));
        langLabel.setText(Locale.getDefault().getDisplayLanguage());
        LanguageTranslator translator = I18N.getLanguagePack().getTranslator();
        if (translator != null) langTranslatorLabel.setText(translator.getDisplayName(Locale.getDefault()));
        else langTranslatorLabel.setText("?");
    }

    private void setDefaults() {
        gitHubBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.GITHUB_BOX));
        showDepsBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CODE_BRACES));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
        setData();
    }
}
