package com.dansoftware.libraryapp.gui.updateview.segment.download;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.updateview.UpdateDialog;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.DownloadableBinary;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.jfilegoodies.explorer.FileExplorers;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
    private VBox radioBtnVBox;
    @FXML
    private TextField downloadPathField;
    @FXML
    private ProgressBar progressBar;
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

    /**
     * The {@link TaskbarProgressbar} object that lets us to display a progress on the
     * taskbar (on Windows).
     */
    private TaskbarProgressbar taskbarProgressbar;

    /**
     * The last selected directory where the user wants to save the update.
     */
    private File downloadDirectory;

    /**
     * The {@link ToggleGroup} that is the group of the {@link RadioButton} objects used
     * for selecting the downloadable binary-type.
     */
    private ToggleGroup radioGroup;

    private final Context context;
    private final UpdateInformation updateInformation;
    private final DownloaderTaskExecutor downloaderTaskExecutor;

    DownloadSegmentController(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        this.context = Objects.requireNonNull(context);
        this.updateInformation = Objects.requireNonNull(updateInformation);
        this.downloaderTaskExecutor = new DownloaderTaskExecutor(context, updateInformation, this);
    }

    @FXML
    private void openDirChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.downloadDirectory = directoryChooser.showDialog(context.getContextWindow());

        //if the file exists we set it's absolute-path into the textfield,
        //otherwise we just set an empty string ("") into it.
        this.downloadPathField.setText(Optional
                .ofNullable(downloadDirectory)
                .map(File::getAbsolutePath)
                .orElse(StringUtils.EMPTY)
        );
    }

    @FXML
    private void downloadSelected() {
        //creating a TaskbarProgressbar object if we haven't already
        showTaskbarIndeterminateProgress();
        BinaryEntryRadioButton selectedRadio = (BinaryEntryRadioButton) this.radioGroup.getSelectedToggle();
        downloaderTaskExecutor.start(selectedRadio.binary, downloadDirectory);
    }

    @FXML
    private void pauseDownload() {
        //we pause if it's running; or we start if it's paused
        downloaderTaskExecutor.setPaused(!downloaderTaskExecutor.isPaused());

        //we set the right icon depending on it's paused or not
        downloadPauseBtn.setGraphic(new MaterialDesignIconView(
                downloaderTaskExecutor.isPaused() ?
                        MaterialDesignIcon.PLAY :
                        MaterialDesignIcon.PAUSE
        ));

        //changing the tooltip on the download/pause button
        downloadPauseBtn.setTooltip(new Tooltip(
                downloaderTaskExecutor.isPaused() ?
                        I18N.getGeneralWord("update.view.download.tooltip.resume") :
                        I18N.getGeneralWord("update.view.download.tooltip.pause")
        ));
    }

    @FXML
    private void killDownload() {
        //we cancel the task
        downloaderTaskExecutor.kill();
    }

    @FXML
    private void openDownloaded() {
        File result = downloaderTaskExecutor.getResult();
        if (result != null)
            FileExplorers.getLazy().openSelect(result);
    }

    @FXML
    private void runDownloaded() {
        File result = downloaderTaskExecutor.getResult();
        if (result != null) {
            try {
                Runtime.getRuntime().exec(result.getAbsoluteFile().getAbsolutePath());
            } catch (IOException e) {
                context.showErrorDialog(
                        I18N.getAlertMsg("update.view.downloaded.run.failed.title", result.getName()),
                        I18N.getAlertMsg("update.view.downloaded.run.failed.msg"), e, buttonType -> {
                        });
            }
        }
    }

    private void showTaskbarIndeterminateProgress() {
        if (taskbarProgressbar == null)
            taskbarProgressbar = TaskbarProgressbarFactory.getTaskbarProgressbar((Stage) context.getContextWindow());
        taskbarProgressbar.showIndeterminateProgress();
    }

    private void createFormatChooserRadioButtons() {
        this.radioGroup = new ToggleGroup();
        updateInformation.getBinaries().forEach(binary ->
                radioBtnVBox.getChildren().add(new BinaryEntryRadioButton(radioGroup, binary)));
    }

    UpdateDialog getDialog() {
        return (UpdateDialog) this.root.getParent().getParent();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.createFormatChooserRadioButtons();
        this.downloadBtn.disableProperty().bind(
                this.radioGroup.selectedToggleProperty().isNull().or(this.downloadPathField.textProperty().isEmpty()));
        this.downloadPathChooserBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.FOLDER));
        this.downloadPauseBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PAUSE));
        this.downloadKillBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.STOP));
    }

    /**
     * A {@link BinaryEntryRadioButton} is a {@link RadioButton} that represents a
     * binary-type.
     *
     * <p>
     * It requires a {@link ToggleGroup} and a {@link DownloadableBinary} object that represents the
     * downloadable update-file.
     */
    private static class BinaryEntryRadioButton extends RadioButton {

        private final DownloadableBinary binary;

        public BinaryEntryRadioButton(@NotNull ToggleGroup toggleGroup,
                                      @NotNull DownloadableBinary binary) {
            this.binary = binary;
            this.setText(binary.getName());
            this.setToggleGroup(toggleGroup);
        }
    }

    ProgressBar getProgressBar() {
        return progressBar;
    }

    Button getDownloadPathChooserBtn() {
        return downloadPathChooserBtn;
    }

    Button getFileOpenerBtn() {
        return fileOpenerBtn;
    }

    Button getRunnerBtn() {
        return runnerBtn;
    }

    Button getDownloadBtn() {
        return downloadBtn;
    }

    Button getDownloadPauseBtn() {
        return downloadPauseBtn;
    }

    Button getDownloadKillBtn() {
        return downloadKillBtn;
    }

    TaskbarProgressbar getTaskbarProgressbar() {
        return taskbarProgressbar;
    }

    VBox getRoot() {
        return root;
    }
}


