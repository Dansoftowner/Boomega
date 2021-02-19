package com.dansoftware.boomega.gui.updatedialog.segment.download;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.updatedialog.UpdateDialog;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.DownloadableBinary;
import com.dansoftware.boomega.update.UpdateInformation;
import com.jfilegoodies.FileGoodies;
import com.jfilegoodies.explorer.FileExplorers;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
        context.showIndeterminateProgress();
        BinaryEntryRadioButton selectedRadio = (BinaryEntryRadioButton) this.radioGroup.getSelectedToggle();
        downloaderTaskExecutor.start(selectedRadio.binary, downloadDirectory);
    }

    @FXML
    private void pauseDownload() {
        //we pause if it's running; or we start if it's paused
        downloaderTaskExecutor.startPause();

        //we set the right icon depending on it's paused or not
        downloadPauseBtn.setGraphic(new MaterialDesignIconView(
                downloaderTaskExecutor.isPaused() ?
                        MaterialDesignIcon.PLAY :
                        MaterialDesignIcon.PAUSE
        ));

        //changing the tooltip on the download/pause button
        downloadPauseBtn.setTooltip(new Tooltip(
                downloaderTaskExecutor.isPaused() ?
                        I18N.getValues().getString("update.view.download.resume") :
                        I18N.getValues().getString("update.view.download.pause")
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
                    I18N.getValue("update.view.downloaded.run.failed.title", result.getName()),
                    I18N.getValue("update.view.downloaded.run.failed.msg"), e, buttonType -> {
                    });
        }
    }

    private void createFormatChooserRadioButtons() {
        this.radioGroup = new ToggleGroup();
        List<DownloadableBinary> binaries = updateInformation.getBinaries();
        if (!binaries.isEmpty()) {
            binaries.forEach(binary ->
                    radioBtnVBox.getChildren().add(new BinaryEntryRadioButton(radioGroup, binary)));
        } else {
            radioBtnVBox.getChildren().add(new NoBinaryAvailablePlaceHolder());
        }
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

    private static final class NoAvailableBinariesPlaceHolder extends StackPane {
        NoAvailableBinariesPlaceHolder() {
//            getChildren()
        }
    }

    /**
     * A {@link BinaryEntryRadioButton} is a {@link RadioButton} that represents a
     * binary-type.
     *
     * <p>
     * It requires a {@link ToggleGroup} and a {@link DownloadableBinary} object that represents the
     * downloadable update-file.
     */
    private static final class BinaryEntryRadioButton extends RadioButton {

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

    TextField getDownloadPathField() {
        return downloadPathField;
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

    VBox getRadioBtnVBox() {
        return radioBtnVBox;
    }

    VBox getRoot() {
        return root;
    }
}


