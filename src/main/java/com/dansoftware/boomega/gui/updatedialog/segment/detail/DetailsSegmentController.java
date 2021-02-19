package com.dansoftware.boomega.gui.updatedialog.segment.detail;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.UpdateInformation;
import com.sandec.mdfx.MDFXNode;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the {@link DetailsSegment}.
 *
 * @author Daniel Gyorffy
 */
public class DetailsSegmentController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DetailsSegmentController.class);

    @FXML
    private ScrollPane previewScrollPane;

    private final Context context;
    private final UpdateInformation updateInformation;

    public DetailsSegmentController(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        this.context = Objects.requireNonNull(context);
        this.updateInformation = Objects.requireNonNull(updateInformation);
    }

    private void loadPreview() {
        String reviewUrl = updateInformation.getReviewUrl();
        new PreviewDownloadThread(reviewUrl).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewScrollPane.setFitToWidth(true);
        loadPreview();
    }

    /**
     * A PreviewErrorPlaceHolder is a GUI object that is used as a placeholder for
     * the preview-area.
     */
    private final class PreviewErrorPlaceHolder extends StackPane {
        PreviewErrorPlaceHolder(@NotNull Throwable cause) {
            var label = new Label(I18N.getValue("update.view.details.preview.failed"));
            var detailBtn = new Button(I18N.getValue("update.view.details.preview.failed.more"));
            detailBtn.setOnAction(event -> {
                DetailsSegmentController.this.context
                        .showErrorDialog(I18N.getValue("update.view.details.preview.failed"),
                                null, (Exception) cause, buttonType -> {
                                });
            });

            var vBox = new VBox(5, label, detailBtn);
            var root = new Group(vBox);
            this.getChildren().add(root);
        }
    }

    private final class PreviewDownloadThread extends Thread {
        PreviewDownloadThread(@NotNull String url) {
            super(new PreviewTextDownloaderTask(url));
            setName(getName() + " PreviewDownloadThread");
            setDaemon(true);
        }
    }

    /**
     * A PreviewTextDownloaderTask defines a process to download the markdown-text that describes the
     * new features of the update from the internet by the specified URL. While the download is in progress
     * it will display a progressbar for the user. If the task failed then it will display an error message
     * for the user (by the help of {@link DetailsSegmentController.PreviewErrorPlaceHolder}). When the download is completed successfully,
     * it will display it in a javaFX node ({@link MDFXNode}) that renders the Markdown-text graphically.
     * <p>
     * It should be executed on a background-thread to get it work properly.
     *
     * <pre>{@code
     * String url = ...; // with the http(s) protocol
     * var task = new RawTextDownloaderTask(url);
     * new Thread(task).start();
     * }</pre>
     * <p>
     * We can handle the result by using the methods defined in {@link Task}
     * ({@link Task#setOnSucceeded(EventHandler)}, {@link Task#setOnFailed(EventHandler)} etc...)
     *
     * @see Task
     */
    private class PreviewTextDownloaderTask extends Task<String> {

        private final String url;

        public PreviewTextDownloaderTask(@NotNull String url) {
            this.url = url;

            //while the task downloads the data, we show an indeterminate progress-bar
            setOnRunning(e -> {
                ProgressBar progressBar = new ProgressBar();
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                DetailsSegmentController.this.previewScrollPane.setContent(progressBar);
            });

            //if the task failed we log the error message and we also show a message to the user
            setOnFailed(e -> {
                Throwable cause = e.getSource().getException();
                logger.error("Couldn't load the markdown-preview", cause);
                DetailsSegmentController.this.previewScrollPane.setContent(new PreviewErrorPlaceHolder(cause));
            });

            //if the task succeeded, we render it as a markdown-text into a javaFX node
            setOnSucceeded(e -> {
                String markdownRaw = getValue();
                // rendering the markdown-text into a javaFX node:
                var markdownDisplay = new MDFXNode(markdownRaw);
                DetailsSegmentController.this.previewScrollPane.setContent(markdownDisplay);
                DetailsSegmentController.this.previewScrollPane.setFitToHeight(false);
                DetailsSegmentController.this.previewScrollPane.setFitToWidth(true);
            });
        }

        @Override
        protected String call() throws Exception {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            try (var input = new BufferedInputStream(urlConnection.getInputStream())) {
                StringBuilder stringBuilder = new StringBuilder();

                byte[] buf = new byte[500];
                int bytesRead;
                while ((bytesRead = input.read(buf)) >= 0)
                    stringBuilder.append(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
    }
}
