package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.gui.util.WindowUtils;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.nativejavafx.taskbar.TaskbarProgressbar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class UpdatePageDownload extends UpdatePage {

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

    private ToggleGroup radioGroup;

    public UpdatePageDownload(@NotNull UpdateView updateView, @NotNull UpdatePage previous, @NotNull UpdateInformation information) {
        super(updateView, previous, information, UpdatePageDownload.class.getResource("UpdatePageDownload.fxml"));
    }

    private void createFormatChooserRadioButtons() {
        super.getInformation()
                .getBinaries()
                .forEach((key, value) -> {
                    radioBtnVBox.getChildren().add(new BinaryEntryRadioButton(radioGroup, key, value));
                });
    }

    @FXML
    private void openDirChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(WindowUtils.getWindowOf(this));

    }

    @FXML
    private void downloadSelected() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.radioGroup = new ToggleGroup();
        this.createFormatChooserRadioButtons();
        this.downloadBtn.disableProperty().bind(this.radioGroup.selectedToggleProperty().isNull());
        this.downloadPathChooserBtn.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.FOLDER));
    }

    private class BinaryDownloaderTask extends Task<File> {

        private final String url;
        private final File dir;

        public BinaryDownloaderTask(@NotNull String url, @NotNull File dir) {
            this.url = url;
            this.dir = dir;
        }

        @Override
        protected File call() throws Exception {

            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            String fileName = FilenameUtils.getName(url.getPath());
            File outputFile = new File(this.dir, fileName);

            try(var input = new BufferedInputStream(connection.getInputStream());
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

                    double percent = allReadBytesCount / (double) onePercent;
                    updateProgress(percent, 100d);

                    if (this.isCancelled()) {
                        updateProgress(0, 0);
                        return null;
                    }
                }

                return outputFile;
            } finally {
                connection.disconnect();
            }
        }
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
}
