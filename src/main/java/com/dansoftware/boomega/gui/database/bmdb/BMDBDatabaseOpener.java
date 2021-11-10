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

package com.dansoftware.boomega.gui.database.bmdb;

import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.database.bmdb.BMDBMeta;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A DatabaseOpener is used for opening already existing database files into their
 * java object representation ({@link DatabaseMeta}).
 *
 * @author Daniel Gyorffy
 */
public class BMDBDatabaseOpener {

    public BMDBDatabaseOpener() {
    }

    @NotNull
    private List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return List.of(
                new FileChooser.ExtensionFilter("Boomega database files", "*." + System.getProperty("boomega.file.extension")),
                new FileChooser.ExtensionFilter("All files", "*")
        );
    }

    @NotNull
    private FileChooser createFileChooser() {
        List<FileChooser.ExtensionFilter> extensionFilters = getExtensionFilters();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(extensionFilters);
        fileChooser.setSelectedExtensionFilter(extensionFilters.get(0));

        return fileChooser;
    }

    /**
     * Shows a {@link FileChooser} where the user can select multiple database files.
     *
     * <p>
     * <b>It should be invoked on the UI thread.</b>
     *
     * @param ownerWindow the owner-window for the {@link FileChooser}
     * @return the selected databases in a {@link List}
     */
    @NotNull
    public List<DatabaseMeta> showMultipleOpenDialog(@Nullable Window ownerWindow) {
        List<File> files = createFileChooser().showOpenMultipleDialog(ownerWindow);
        if (CollectionUtils.isNotEmpty(files)) {
            return files.stream()
                    .map(BMDBMeta::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * Shows a {@link FileChooser} where the user can select a database file.
     *
     * <p>
     * <b>It should be invoked on the UI thread.</b>
     *
     * @param ownerWindow the owner-window for the {@link FileChooser}
     * @return the selected database; may be null
     */
    @Nullable
    public DatabaseMeta showOpenDialog(@Nullable Window ownerWindow) {
        File file = createFileChooser().showOpenDialog(ownerWindow);
        if (file != null) {
            return new BMDBMeta(file);
        }

        return null;
    }
}
