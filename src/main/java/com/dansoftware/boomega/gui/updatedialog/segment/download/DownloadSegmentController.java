package com.dansoftware.boomega.gui.updatedialog.segment.download;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.control.WebsiteHyperLink;
import com.dansoftware.boomega.gui.updatedialog.UpdateDialog;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.UpdateInformation;
import com.dansoftware.boomega.util.CommonDirectories;
import com.jfilegoodies.FileGoodies;
import com.jfilegoodies.explorer.FileExplorers;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import kotlin.Unit;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for {@link DownloadSegment}.
 *
 * @author Daniel Gyorffy
 */
public class DownloadSegmentController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DownloadSegmentController.class);

    @FXML
    private VBox root;
    @FXML
    private ScrollPane packageScrollPane;
    @FXML
    private HBox sizeIndicator;
    @FXML
    private Label sizeLabel;
    @FXML
    private TextField downloadPathField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private StackPane progressIndicator;
    @FXML
    private Label progressMessageLabel;
    @FXML
    private Label progressTitleLabel;
    @FXML
    private Button downloadPathChooserBtn;
    @FXML
    private Button fileOpenerBtn;
    @FXML
    private Button runnerBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private Button downloadPauseBtn;
    @FXML
    private Button downloadKillBtn;

    private PackageChooserArea packageChooserArea;

    /**
     * The last selected directory where the user wants to save the update.
     */
    private final ObjectProperty<File> downloadDirectory =
            new SimpleObjectProperty<>();

    private final Context context;
    private final UpdateInformation updateInformation;

    private final DownloaderTaskFactory taskFactory;

    DownloadSegmentController(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        this.context = Objects.requireNonNull(context);
        this.updateInformation = Objects.requireNonNull(updateInformation);
        this.taskFactory = new DownloaderTaskFactory(context, updateInformation);
    }

    @FXML
    private void openDirChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        //if the file exists we set it's absolute-path into the textfield,
        //otherwise we just set an empty string ("") into it.
        this.downloadPathField.setText(Optional
                .ofNullable(directoryChooser.showDialog(context.getContextWindow()))
                .map(File::getAbsolutePath)
                .orElse(StringUtils.EMPTY)
        );
    }

    @FXML
    private void downloadSelected() {
        //creating a TaskbarProgressbar object if we haven't already
        context.showIndeterminateProgress();
        taskFactory.start(packageChooserArea.getSelectedBinary(), downloadDirectory.get());
    }

    @FXML
    private void pauseDownload() {
        //we pause if it's running; or we start if it's paused
        taskFactory.startPause();
    }

    @FXML
    private void killDownload() {
        //we cancel the task
        taskFactory.kill();
    }

    @FXML
    private void openDownloaded() {
        File result = taskFactory.getFile();
        if (result != null)
            FileExplorers.getLazy().openSelect(result);
    }

    @FXML
    private void runDownloaded() {
        File result = taskFactory.getFile();
        try {
            if (result != null) {

                if (FileGoodies.isOSExecutable(result))
                    Runtime.getRuntime().exec(result.getAbsoluteFile().getAbsolutePath());
                else
                    openDownloaded();
                Platform.exit();

            }
        } catch (IOException e) {
            context.showErrorDialog(
                    I18N.getValue("update.dialog.download.install.failed.title"),
                    I18N.getValue("update.dialog.download.install.failed.msg", result.getName()),
                    e,
                    buttonType -> {}
            );
        }
    }

    private void buildPackageChooserArea() {
        packageChooserArea = new PackageChooserArea();
        updateInformation.getBinaries().forEach(packageChooserArea::putEntry);
        if (packageChooserArea.isEmpty())
            packageScrollPane.setContent(new NoBinaryAvailablePlaceHolder());
        else
            packageScrollPane.setContent(packageChooserArea);
    }

    private void buildWebsiteHyperlink() {
        root.getChildren().add(
                new StackPane(
                        new WebsiteHyperLink(
                                I18N.getValue("website.open"),
                                updateInformation.getWebsite()
                        )
                )
        );
    }

    UpdateDialog getDialog() {
        return (UpdateDialog) this.root.getParent().getParent();
    }

    private void initDownloadButtonBehaviour() {
        this.downloadBtn.disableProperty().bind(
                this.packageChooserArea.selectedBinaryProperty().isNull()
                        .or(this.downloadPathField.textProperty().isEmpty())
                        .or(this.taskFactory.getRunningProperty())
        );
    }

    private void initSizeIndicatorBehaviour() {
        sizeIndicator.visibleProperty().bind(packageChooserArea.selectedBinaryProperty().isNotNull());
        sizeIndicator.managedProperty().bind(packageChooserArea.selectedBinaryProperty().isNotNull());
        packageChooserArea.selectedBinaryProperty().addListener((observable, oldValue, pack) ->
                sizeLabel.setText(String.format("%d MB", pack.getSize() / 1024)));
    }

    private void initProgressbarBehaviour() {
        progressBar.progressProperty().bind(taskFactory.getProgressProperty());
        progressBar.managedProperty().bind(taskFactory.getRunningProperty());
        progressBar.visibleProperty().bind(taskFactory.getRunningProperty());
        taskFactory.getPausedProperty().addListener((observable, oldValue, running) -> {
            if (running) progressBar.getStyleClass().add("paused");
            else progressBar.getStyleClass().remove("paused");
        });
    }

    private void initProgressIndicatorBehaviour() {
        progressIndicator.managedProperty().bind(taskFactory.getRunningProperty());
        progressIndicator.visibleProperty().bind(taskFactory.getRunningProperty());
    }

    private void initProgressMessageLabelBehaviour() {
        progressMessageLabel.textProperty().bind(taskFactory.getMessageProperty());
    }

    private void initProgressTitleLabelBehaviour() {
        progressTitleLabel.textProperty().bind(taskFactory.getTitleProperty());
    }

    private void initPauseButtonBehaviour() {
        downloadPauseBtn.disableProperty().bind(taskFactory.getRunningProperty().not());
        taskFactory.getPausedProperty().addListener((observable, oldValue, isPaused) -> {
            downloadPauseBtn.setGraphic(new MaterialDesignIconView(isPaused ? MaterialDesignIcon.PLAY : MaterialDesignIcon.PAUSE));
            downloadPauseBtn.setTooltip(new Tooltip(I18N.getValue(taskFactory.isPaused() ? "update.dialog.download.resume" : "update.dialog.download.pause")));
        });
    }

    private void initKillButtonBehaviour() {
        downloadKillBtn.disableProperty().bind(taskFactory.getRunningProperty().not());
    }

    private void initPathFieldBehaviour() {
        downloadPathField.textProperty().addListener((observable, oldValue, newValue) -> downloadDirectory.set(new File(newValue)));
        downloadPathField.disableProperty().bind(taskFactory.getRunningProperty());
    }

    private void initDirChooserButtonBehaviour() {
        downloadPathChooserBtn.disableProperty().bind(taskFactory.getRunningProperty());
    }

    private void initOpenerButtonBehaviour() {
        fileOpenerBtn.disableProperty().bind(taskFactory.getWorkDoneProperty().lessThan(100));
    }

    private void initPackageChooserBehaviour() {
        packageChooserArea.disableProperty().bind(taskFactory.getRunningProperty());
    }

    private void initRunnerButtonBehaviour() {
        runnerBtn.disableProperty().bind(taskFactory.getWorkDoneProperty().lessThan(100));
    }


    private void initDialogBehaviour() {
        taskFactory.onNewTaskCreated(task -> {
            UpdateDialog dialog = getDialog();
            dialog.nextButtonDisableProperty().bind(taskFactory.getRunningProperty());
            dialog.prevButtonDisableProperty().bind(taskFactory.getRunningProperty());
            return Unit.INSTANCE;
        });
    }

    private void initBehaviours() {
        initDownloadButtonBehaviour();
        initSizeIndicatorBehaviour();
        initProgressbarBehaviour();
        initProgressIndicatorBehaviour();
        initProgressTitleLabelBehaviour();
        initProgressMessageLabelBehaviour();
        initPauseButtonBehaviour();
        initKillButtonBehaviour();
        initPathFieldBehaviour();
        initDirChooserButtonBehaviour();
        initOpenerButtonBehaviour();
        initRunnerButtonBehaviour();
        initPackageChooserBehaviour();
        initDialogBehaviour();
    }

    private void setIcons() {
        this.downloadPathChooserBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.FOLDER));
        this.downloadPauseBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PAUSE));
        this.downloadKillBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.STOP));
    }

    private void setDefaults() {
        this.downloadPathField.setText(CommonDirectories.getDownloadsDirPath());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildPackageChooserArea();
        buildWebsiteHyperlink();
        initBehaviours();
        setDefaults();
        setIcons();
    }
}


