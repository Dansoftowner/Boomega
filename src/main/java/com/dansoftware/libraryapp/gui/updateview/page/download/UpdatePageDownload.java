package com.dansoftware.libraryapp.gui.updateview.page.download;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.DownloadableBinary;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.jfilegoodies.explorer.FileExplorers;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import com.nativejavafx.taskbar.TaskbarProgressbarFactory;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
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

/**
 * An UpdatePageDownload is an {@link UpdatePage} that is the last page in an {@link UpdateView}
 * after an {@link com.dansoftware.libraryapp.gui.updateview.page.detail.UpdatePageDetail}.
 *
 * <p>
 * Allows the user to download the new program from the internet. The user can select what type
 * of file should be downloaded.
 *
 * @author Daniel Gyorffy
 */
public class UpdatePageDownload extends UpdatePage {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePageDownload.class);

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
     * The currently running {@link DownloaderTask} that downloads the update from the internet.
     */
    private DownloaderTask downloadingTask;

    /**
     * The last selected directory where the user wants to save the update.
     */
    private File downloadDirectory;

    /**
     * The {@link ToggleGroup} that is the group of the {@link RadioButton} objects used
     * for selecting the downloadable binary-type.
     */
    private ToggleGroup radioGroup;

    /**
     * Creates an UpdatePageDownload.
     *
     * @see UpdatePage#UpdatePage(UpdateView, UpdatePage, UpdateInformation, URL)
     */
    public UpdatePageDownload(@NotNull UpdateView updateView,
                              @NotNull UpdatePage previous,
                              @NotNull UpdateInformation information) {
        super(updateView, previous, information, UpdatePageDownload.class.getResource("UpdatePageDownload.fxml"));
    }

    @FXML
    private void openDirChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.downloadDirectory = directoryChooser.showDialog(WindowUtils.getWindowOf(this));

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
        if (taskbarProgressbar == null) {
            taskbarProgressbar = TaskbarProgressbarFactory.getTaskbarProgressbar(WindowUtils.getStageOf(this));
        }

        taskbarProgressbar.showIndeterminateProgress();

        var selectedRadio = (BinaryEntryRadioButton) this.radioGroup.getSelectedToggle();
        this.downloadingTask = this.new DownloaderTask(selectedRadio.binary, this.downloadDirectory);

        var thread = new Thread(downloadingTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void pauseDownload() {
        if (downloadingTask == null)
            return;

        //we pause if it's running; or we start if it's paused
        downloadingTask.setPaused(!downloadingTask.isPaused());

        //we set the right icon depending on it's paused or not
        downloadPauseBtn.setGraphic(new MaterialDesignIconView(
                downloadingTask.isPaused() ?
                        MaterialDesignIcon.PLAY :
                        MaterialDesignIcon.PAUSE
        ));

        //changing the tooltip on the download/pause button
        downloadPauseBtn.setTooltip(new Tooltip(
                downloadingTask.isPaused() ?
                        I18N.getGeneralWord("update.view.download.tooltip.resume") :
                        I18N.getGeneralWord("update.view.download.tooltip.pause")
        ));
    }

    @FXML
    private void killDownload() {
        if (downloadingTask == null)
            return;

        //we cancel the task
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
                        .showErrorDialog(
                                I18N.getAlertMsg("update.view.downloaded.run.failed.title", result.getName()),
                                I18N.getAlertMsg("update.view.downloaded.run.failed.msg"), e, buttonType -> {
                                });
            }
        }
    }

    private void createFormatChooserRadioButtons() {
        this.radioGroup = new ToggleGroup();
        super.getInformation()
                .getBinaries()
                .forEach(binary ->
                        radioBtnVBox.getChildren().add(new BinaryEntryRadioButton(radioGroup, binary)));
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

    /**
     * A DownloaderTask defines a process to download a large file (so it's used for downloading
     * the updates). If the task failed then it will display an error-message.
     *
     * <p>
     * It requires a:
     * <ul>
     *     <li><b>binary</b> that defines what binary should we download</li>
     *     <li><b>dir</b> that defines what directory should we save the file to</li>
     * </ul>
     */
    private final class DownloaderTask extends Task<File> {

        /**
         * The object that the DownloaderTask use for thread-locking
         */
        private final Object lock = new Object();

        /**
         * The boolean that indicates that the task's background thread is paused
         */
        private volatile boolean paused;

        private final DownloadableBinary binary;

        private final File dir;

        /**
         * Creates a normal {@link DownloaderTask}.
         *
         * @param binary the {@link DownloadableBinary} object that defines what to download
         * @param dir    the directory where we want to save the downloaded binary
         */
        DownloaderTask(@NotNull DownloadableBinary binary, @NotNull File dir) {
            this.binary = binary;
            this.dir = dir;
            this.progressProperty().addListener((observable, oldValue, newValue) -> {
                UpdatePageDownload.this.taskbarProgressbar
                        .showCustomProgress((long) getWorkDone(), (long) getTotalWork(),
                                isPaused() ? TaskbarProgressbar.Type.PAUSED : TaskbarProgressbar.Type.NORMAL);
            });

            setOnRunning(e -> {
                //we bind the progressbar's progress property to the task's progress
                UpdatePageDownload.this.progressBar.progressProperty().unbind();
                UpdatePageDownload.this.progressBar.progressProperty().bind(this.progressProperty());

                //the pause button will only be available if the task is running
                UpdatePageDownload.this.downloadPauseBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadPauseBtn.disableProperty().bind(this.runningProperty().not());

                //the download-stopper button will only be available if the task is running
                UpdatePageDownload.this.downloadKillBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadKillBtn.disableProperty().bind(this.runningProperty().not());

                //the download-button will be disabled while the task is running
                UpdatePageDownload.this.downloadBtn.disableProperty().unbind();
                UpdatePageDownload.this.downloadBtn.disableProperty().bind(this.runningProperty());

                //the downloaded-file-opener button will be disabled while the progress is less than 100
                UpdatePageDownload.this.fileOpenerBtn.disableProperty().unbind();
                UpdatePageDownload.this.fileOpenerBtn.disableProperty().bind(this.workDoneProperty().lessThan(100));

                //the downloaded-file-runner button will be disabled while the progress is less than 100
                UpdatePageDownload.this.runnerBtn.disableProperty().unbind();
                UpdatePageDownload.this.runnerBtn.disableProperty().bind(this.workDoneProperty().lessThan(100));

                //while the task is running the UpdateView can't be closed
                getUpdateView().getCloseBtn().disableProperty().bind(this.runningProperty());

                //while the task is running the UpdateView can't navigate to a previous update-page
                getUpdateView().getPrevBtn().disableProperty().bind(this.runningProperty());

                //creating a node that has two labels: one on the left, one on the right
                //the label on the left displays the actual message, the label on the right displays
                //the actual progress
                Node progressIndicatorNode = new StackPane(new Label() {{
                    textProperty().bind(DownloaderTask.this.titleProperty());
                    StackPane.setAlignment(this, Pos.CENTER_RIGHT);
                }}, new Label() {{
                    textProperty().bind(DownloaderTask.this.messageProperty());
                    StackPane.setAlignment(this, Pos.CENTER_LEFT);
                }});

                UpdatePageDownload.this.root.getChildren().add(6, progressIndicatorNode);
            });

            setOnCancelled(e -> {
                //stopping the progress on the taskbar
                UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                this.clear();
            });

            setOnFailed(e -> {
                //showing the progress with error-color on the taskbar
                UpdatePageDownload.this.taskbarProgressbar
                        .showCustomProgress((long) getWorkDone(), (long) getTotalWork(), TaskbarProgressbar.Type.ERROR);
                //getting the Exception that caused to fail
                Throwable cause = e.getSource().getException();
                //logging the exception
                logger.error("Something went wrong during the update-downloading ", cause);

                //displaying the error on the gui for the user
                UpdatePageDownload.this
                        .getUpdateView()
                        .getContext()
                        .showErrorDialog(
                                I18N.getAlertMsg("update.view.download.failed.title"),
                                I18N.getAlertMsg("update.view.download.failed.msg"),
                                (Exception) cause, buttonType -> {
                                    UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                                });
                this.clear();
            });

            setOnSucceeded(e -> {
                UpdatePageDownload.this.taskbarProgressbar.stopProgress();
                WindowUtils.getWindowOptionalOf(UpdatePageDownload.this)
                        .ifPresent(Window::requestFocus);
                this.clear();
            });
        }

        private void clear() {
            //clearing the progressbar
            UpdatePageDownload.this.progressBar.progressProperty().unbind();
            UpdatePageDownload.this.progressBar.setProgress(0);
            //removing the progress indicator label
            UpdatePageDownload.this.root.getChildren().remove(6);
        }

        /**
         * Pauses or resumes the downloader-thread depending on the boolean value
         *
         * @param paused {@code true} if the downloader-thread should be paused;
         *               {@code false} if the downloader-thread should be resumed
         */
        void setPaused(boolean paused) {
            //if the paused's value is changed AND the paused's value is false,
            //we notify the thread-locker object to awake the downloading thread
            if (this.paused != paused && !(this.paused = paused)) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        }

        boolean isPaused() {
            return this.paused;
        }

        /**
         * Helper method to define the file that we want to save the downloaded file to.
         *
         * @param binary the object that represents the downloadable binary
         * @param dir    the directory where we want to save the file
         * @return the {@link File} object that represents the file that we should save to
         */
        private File getOutputFile(DownloadableBinary binary, File dir) {
            String fileName = FilenameUtils.getName(binary.getDownloadUrl());
            if (StringUtils.isBlank(fileName)) {
                fileName = "libraryapp-" + getInformation().getVersion() + "." + binary.getFileExtension();
            } else if (!StringUtils.endsWithIgnoreCase(fileName, binary.getFileExtension())) {
                fileName += (fileName.endsWith(".") ? StringUtils.EMPTY : ".") + binary.getFileExtension();
            }

            return new File(dir, fileName);
        }

        @Override
        protected File call() throws IOException, InterruptedException {
            synchronized (lock) {
                //creating the URLConnection
                URL url = new URL(this.binary.getDownloadUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                //defining the file that we want to write to
                File outputFile = getOutputFile(this.binary, this.dir);

                //creating the input-, and output-stream
                try (var input = new BufferedInputStream(connection.getInputStream());
                     var output = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                    updateMessage(I18N.getProgressMessage("update.page.download.happening"));

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

                        //we check that a one-percent value is not a false value
                        //then we update the progress
                        if (onePercent > 0) {
                            double percent = allReadBytesCount / (double) onePercent;
                            updateProgress(percent, 100d);
                            updateTitle(String.format("%d/%d%%", (int) percent, 100));
                        }

                        //if the user cancelled the task, we return
                        if (this.isCancelled()) {
                            updateProgress(0, 0);
                            return null;
                        }

                        //if a pause request detected, we pause the thread
                        // by the thread-locker object's wait() method
                        if (this.paused) {
                            updateMessage(I18N.getProgressMessage("update.page.download.paused"));
                            lock.wait();
                            updateMessage(I18N.getProgressMessage("update.page.download.happening"));
                        }
                    }

                    updateProgress(100, 100);

                    return outputFile;
                } finally {
                    connection.disconnect();
                }
            }
        }
    }
}
