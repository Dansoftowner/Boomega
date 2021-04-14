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

package com.dansoftware.boomega.gui.updatedialog.segment.detail;

import com.dansoftware.boomega.update.UpdateInformation;
import com.sandec.mdfx.MDFXNode;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;

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
class PreviewTextLoadTask extends Task<String> {

    private final UpdateInformation information;

    public PreviewTextLoadTask(@NotNull UpdateInformation information) {
        this.information = information;
    }

    @Override
    protected String call() throws Exception {
        try (var input = new BufferedReader(information.reviewReader())) {
            StringBuilder stringBuilder = new StringBuilder();

            char[] buf = new char[250];
            int charsRead;
            while ((charsRead = input.read(buf)) >= 0)
                stringBuilder.append(new String(buf, 0, charsRead));

            return stringBuilder.toString();
        }
    }
}
