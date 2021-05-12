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
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;

/**
 * A PreviewTextDownloaderTask defines a process to download the markdown-text that describes the
 * new features of the update from the internet by the specified URL.
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
