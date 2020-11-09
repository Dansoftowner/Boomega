package com.dansoftware.libraryapp.gui.updateview.segment.download;

import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.DownloadableBinary;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Task for downloading the particular update package from the server.
 *
 * @author Daniel Gyorffy
 */
public class DownloaderTask extends Task<File> {

    /**
     * The object that the DownloaderTask use for thread-locking
     */
    private final Object lock = new Object();

    /**
     * The boolean that indicates that the task's background thread is paused
     */
    private volatile boolean paused;
    private final UpdateInformation updateInformation;
    private final DownloadableBinary binary;
    private final File dir;

    /**
     * Creates a normal {@link DownloaderTask}.
     *
     * @param binary the {@link DownloadableBinary} object that defines what to download
     * @param dir    the directory where we want to save the downloaded binary
     */
    DownloaderTask(@NotNull UpdateInformation updateInformation,
                   @NotNull DownloadableBinary binary,
                   @NotNull File dir) {
        this.updateInformation = updateInformation;
        this.binary = binary;
        this.dir = dir;
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
            fileName = "libraryapp-" + updateInformation.getVersion() + "." + binary.getFileExtension();
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
