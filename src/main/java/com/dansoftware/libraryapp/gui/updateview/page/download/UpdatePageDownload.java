package com.dansoftware.libraryapp.gui.updateview.page.download;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.jfilegoodies.explorer.FileExplorers;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UpdatePageDownload extends UpdatePage {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePageDownload.class);

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

    private TaskbarProgressbar taskbarProgressbar;

    private DownloaderTask downloadingTask;

    private File downloadDirectory;

    private ToggleGroup radioGroup;

    public UpdatePageDownload(@NotNull UpdateView updateView, @NotNull UpdatePage previous, @NotNull UpdateInformation information) {
        super(updateView, previous, information, UpdatePageDownload.class.getResource("UpdatePageDownload.fxml"));
    }


    @FXML
    private void openDirChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.downloadDirectory = directoryChooser.showDialog(WindowUtils.getWindowOf(this));

        this.downloadPathField.setText(
                Optional.ofNullable(downloadDirectory)
                        .map(File::getAbsolutePath)
                        .orElse(StringUtils.EMPTY)
        );
    }

    @FXML
    private void downloadSelected() {
        if (taskbarProgressbar == null) {
            taskbarProgressbar = TaskbarProgressbarFactory.getTaskbarProgressbar(WindowUtils.getStageOf(this));
        }

        taskbarProgressbar.showIndeterminateProgress();

        var selectedRadio = (BinaryEntryRadioButton) this.radioGroup.getSelectedToggle();
        this.downloadingTask = this.new DownloaderTask(selectedRadio.url, this.downloadDirectory);

        var thread = new Thread(downloadingTask);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    private void pauseDownload() {
        if (downloadingTask == null)
            return;

        downloadingTask.setPaused(!downloadingTask.isPaused());

        downloadPauseBtn.setGraphic(new MaterialDesignIconView(
                downloadingTask.isPaused() ? MaterialDesignIcon.PLAY : MaterialDesignIcon.PAUSE
        ));

        /*downloadPauseBtn.setTooltip(new Tooltip(
                downloadingTask.isPaused() ? I18N.getGeneralWord("") : I18N.getGeneralWord("")
        ));*/
    }

    @FXML
    private void killDownload() {
        if (downloadingTask == null)
            return;

        downloadingTask.setPaused(false);
        downloadingTask.cancel();
    }

    @FXML
    private void openDownloaded() {
        if (downloadingTask != null && downloadingTask.isDone()) {
            File result = downloadingTask.getValue();
            FileExplorers.getLazy().openSelect(result);
        }
    }

    @FXML
    private void runDownloaded() {
        if (downloadingTask != null && downloadingTask.isDone()) {
            File result = downloadingTask.getValue();
            try {
                Runtime.getRuntime().exec(result.getAbsoluteFile().getAbsolutePath());
            } catch (IOException e) {
                getUpdateView().getContext()
                        .showErrorDialog("", "", e, buttonType -> {
                        });
            }
        }
    }

    private void createFormatChooserRadioButtons() {
        this.radioGroup = new ToggleGroup();
        super.getInformation()
                .getBinaries()
                .forEach((key, value) -> {
                    radioBtnVBox.getChildren().add(new BinaryEntryRadioButton(radioGroup, key, value));
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.createFormatChooserRadioButtons();
        this.downloadBtn.disableProperty().bind(
                this.radioGroup.selectedToggleProperty().isNull().or(
                        this.downloadPathField.textProperty().isEmpty()
                )
        );

        this.downloadPathChooserBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.FOLDER));
        this.downloadPauseBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PAUSE));
        this.downloadKillBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.STOP));
    }

    private static class BinaryEntryRadioButton extends RadioButton {

        private final String url;

        public BinaryEntryRadioButton(ToggleGroup toggleGroup, String binaryName, String downloadUrl) {
            this.setText(binaryName);
            this.url = downloadUrl;
            this.setToggleGroup(toggleGroup);
        }

        public String getUrl() {
            return url;
        }
    }

    private final class DownloaderTask extends Task<File> {

        private final String url;
        private final File dir;

        private volatile boolean paused;

        public DownloaderTask(@NotNull String url, @NotNull File dir) {
            this.url = url;
            this.dir = dir;
            this.progressProperty().addListener((observable, oldValue, newValue) -> {
                UpdatePageDownload.this.taskbarProgressbar
                        .showCustomProgress((long) getWorkDone(), (long) getTotalWork(),
                                isPaused() ? TaskbarProgressbar.Type.PAUSED : TaskbarProgressbar.Type.NORMAL);
            });

            setOnRunning(e -> {
                UpdatePageDownload.this.progressBar.progressProperty().unbind();
                UpdatePageDownload.this.progressBar.progressProperty().bind(this.progressProperty());

                UpdatePageDownload.this.downloadPauseBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadPauseBtn.disableProperty().bind(this.runningProperty().not());

                UpdatePageDownload.this.downloadKillBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadKillBtn.disableProperty().bind(this.runningProperty().not());

                UpdatePageDownload.this.downloadBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadBtn.disableProperty().bind(this.runningProperty());

                UpdatePageDownload.this.fileOpenerBtn.disableProperty().unbind();
                UpdatePageDownload.this.fileOpenerBtn.disableProperty().bind(this.workDoneProperty().lessThan(100));

                UpdatePageDownload.this.runnerBtn.disableProperty().unbind();
                UpdatePageDownload.this.runnerBtn.disableProperty().bind(this.workDoneProperty().lessThan(100));

                getUpdateView().getCloseBtn().disableProperty().bind(this.runningProperty());
                getUpdateView().getPrevBtn().disableProperty().bind(this.runningProperty());
            });

            setOnCancelled(e -> {
                UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                UpdatePageDownload.this.progressBar.progressProperty().unbind();
                UpdatePageDownload.this.progressBar.setProgress(0);
            });

            setOnFailed(e -> {
                UpdatePageDownload.this.taskbarProgressbar
                        .showCustomProgress((long) getWorkDone(), (long) getTotalWork(), TaskbarProgressbar.Type.ERROR);
                Throwable cause = e.getSource().getException();
                logger.error("Something went wrong during the update-downloading ", cause);

                UpdatePageDownload.this
                        .getUpdateView()
                        .getContext()
                        .showErrorDialog(
                                I18N.getAlertMsg("update.view.download.failed.title"),
                                I18N.getAlertMsg("update.view.download.failed.msg"),
                                (Exception) cause, buttonType -> {
                                    UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                                });
                UpdatePageDownload.this.progressBar.progressProperty().unbind();
                UpdatePageDownload.this.progressBar.setProgress(0);
            });

            setOnSucceeded(e -> {
                UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                UpdatePageDownload.this.progressBar.progressProperty().unbind();
                UpdatePageDownload.this.progressBar.setProgress(0);
                logger.info("Workdone is : " + getWorkDone());
            });

        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        public boolean isPaused() {
            return this.paused;
        }

        @Override
        protected File call() throws IOException, InterruptedException {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            String fileName = FilenameUtils.getName(url.getPath());
            File outputFile = new File(this.dir, fileName);

            try (var input = new BufferedInputStream(connection.getInputStream());
                 var output = new BufferedOutputStream(new FileOutputStream(outputFile))) {

                //getting the size of the downloadable content
                long contentSize = connection.getContentLengthLong();
                //calculating the value of 1 %
                long onePercent = contentSize / 100;
                //this variable will count that how many bytes are read
                int allReadBytesCount = 0;

                byte[] buf = new byte[2048];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    allReadBytesCount += bytesRead;
                    output.write(buf, 0, bytesRead);

                    if (onePercent != 0) {
                        double percent = allReadBytesCount / (double) onePercent;
                        updateProgress(percent, 100d);
                    }

                    if (this.isCancelled()) {
                        updateProgress(0, 0);
                        return null;
                    }

                    while (this.paused) ;
                }

                updateProgress(100, 100);

                return outputFile;
            } finally {
                connection.disconnect();
            }
        }
    }
}
