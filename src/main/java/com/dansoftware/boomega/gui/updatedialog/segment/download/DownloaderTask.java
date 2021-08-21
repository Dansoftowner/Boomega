/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.updatedialog.segment.download;

import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.Release;
import com.dansoftware.boomega.update.ReleaseAsset;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * Task for downloading the particular update package from the server.
 *
 * @author Daniel Gyorffy
 */
public class DownloaderTask extends Task<File> {

    private static final Logger logger = LoggerFactory.getLogger(DownloaderTask.class);

    /**
     * The object that the DownloaderTask use for thread-locking
     */
    private final Object lock = new Object();

    private final BooleanProperty pausedProperty = new SimpleBooleanProperty();

    /**
     * The boolean that indicates that the task's background thread is paused
     */
    private volatile boolean paused;
    private final Release release;
    private final ReleaseAsset releaseAsset;
    private final File dir;

    /**
     * Creates a normal {@link DownloaderTask}.
     */
    DownloaderTask(@NotNull Release release,
                   @NotNull ReleaseAsset releaseAsset,
                   @NotNull File dir) {
        this.release = release;
        this.releaseAsset = releaseAsset;
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

    BooleanProperty pausedProperty() {
        return pausedProperty;
    }

    @Override
    protected File call() throws IOException, InterruptedException {
        synchronized (lock) {
            logger.debug("Starting downloading the update bundle...");

            //defining the file that we want to write to
            File outputFile = new File(dir, Objects.requireNonNull(releaseAsset.getName()));

            //creating the input-, and output-stream
            try (var input = new BufferedInputStream(releaseAsset.openStream());
                 var output = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                updateMessage(I18N.getValue("update.page.download.happening"));

                long contentSize = this.releaseAsset.getSize();
                logger.debug("Full content size (in bytes): {}", contentSize);

                //calculating the value of 1 %
                long onePercent = contentSize / 100;
                logger.debug("One percent is : {}", onePercent);

                //this variable will count that how many bytes are read
                int allReadBytesCount = 0;

                byte[] buf = new byte[2048];
                int bytesRead;
                logger.debug("Starting loop...");
                while ((bytesRead = input.read(buf)) >= 0) {
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
                        logger.debug("Cancelled during loop");
                        updateProgress(0, 0);
                        return null;
                    }

                    //if a pause request detected, we pause the thread
                    // by the thread-locker object's wait() method
                    if (this.paused) {
                        logger.debug("Paused");
                        Platform.runLater(() -> pausedProperty.set(true));
                        updateMessage(I18N.getValue("update.page.download.paused"));
                        lock.wait();
                        Platform.runLater(() -> pausedProperty.set(false));
                        updateMessage(I18N.getValue("update.page.download.happening"));
                    }
                }
                logger.debug("Ending loop...");

                updateProgress(100, 100);

                logger.debug("Downloader task succeeded: {}", outputFile);
                return outputFile;
            }
        }
    }
}
