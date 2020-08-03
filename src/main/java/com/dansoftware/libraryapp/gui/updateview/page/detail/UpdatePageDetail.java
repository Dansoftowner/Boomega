package com.dansoftware.libraryapp.gui.updateview.page.detail;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.updateview.page.download.UpdatePageDownload;
import com.dansoftware.libraryapp.gui.updateview.page.start.UpdatePageStart;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.sandec.mdfx.MDFXNode;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * An UpdatePageDetail is an {@link UpdatePage} that is the second page in an {@link UpdateView}
 * after an {@link UpdatePageStart}.
 *
 * <p>
 * Downloads the preview markdown-text about the new update and also renders it, so the user can
 * explore the new features and/or bug fixes.
 *
 * It provides a button on the bottom that will navigate to the {@link UpdatePageDownload} page.
 *
 * @author Daniel Gyorffy
 */
public class UpdatePageDetail extends UpdatePage {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePageDetail.class);

    @FXML
    private ScrollPane previewScrollPane;

    /**
     * Creates an UpdatePageDetail that points to a {@link UpdatePageDownload} as next page.
     * 
     * @see UpdatePage#UpdatePage(UpdateView, UpdatePage, UpdateInformation, URL) 
     * @see UpdatePage#setNextPageFactory(Supplier)
     */
    public UpdatePageDetail(@NotNull UpdateView updateView,
                            @NotNull UpdatePage previous,
                            @NotNull UpdateInformation information) {
        super(updateView, previous, information, UpdatePageDetail.class.getResource("UpdatePageDetail.fxml"));
        super.setNextPageFactory(() -> new UpdatePageDownload(getUpdateView(), this, getInformation()));
    }

    /**
     * Loads the description about the new update on a background-thread then displays it
     * on the UI thread using a {@link PreviewTextDownloaderTask}.
     */
    private void loadPreview() {
        new Thread(new PreviewTextDownloaderTask(getInformation().getReviewUrl())).start();
    }

    @FXML
    private void goToNextPage() {
        super.goNext();
    }

    @Override
    public void reload() {
        this.loadPreview();
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
            var label = new Label(I18N.getGeneralWord("update.view.details.preview.failed"));
            var detailBtn = new Button(I18N.getGeneralWord("update.view.details.preview.failed.more"));
            detailBtn.setOnAction(event -> {
                UpdatePageDetail.this
                        .getUpdateView()
                        .getContext()
                        .showErrorDialog(I18N.getGeneralWord("update.view.details.preview.failed"),
                                null, (Exception) cause, buttonType -> {
                                });
            });

            var vBox = new VBox(5, label, detailBtn);
            var root = new Group(vBox);
            this.getChildren().add(root);
        }
    }

    /**
     * A PreviewTextDownloaderTask defines a process to download the markdown-text that describes the
     * new features of the update from the internet by the specified URL. While the download is in progress
     * it will display a progressbar for the user. If the task failed then it will display an error message
     * for the user (by the help of {@link PreviewErrorPlaceHolder}). When the download is completed successfully,
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
                UpdatePageDetail.this.previewScrollPane.setContent(progressBar);
            });

            //if the task failed we log the error message and we also show a message to the user
            setOnFailed(e -> {
                Throwable cause = e.getSource().getException();
                logger.error("Couldn't load the markdown-preview", cause);
                UpdatePageDetail.this.previewScrollPane.setContent(new PreviewErrorPlaceHolder(cause));
            });

            //if the task succeeded, we render it as a markdown-text into a javaFX node
            setOnSucceeded(e -> {
                String markdownRaw = getValue();
                // rendering the markdown-text into a javaFX node:
                var markdownDisplay = new MDFXNode(markdownRaw);
                UpdatePageDetail.this.previewScrollPane.setContent(markdownDisplay);
                UpdatePageDetail.this.previewScrollPane.setFitToHeight(false);
                UpdatePageDetail.this.previewScrollPane.setFitToWidth(true);
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
                while ((bytesRead = input.read(buf)) > 0) {
                    stringBuilder.append(new String(buf, 0, bytesRead, StandardCharsets.UTF_8));
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        }
    }
}
