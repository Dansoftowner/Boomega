package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
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
public class DatabaseOpener {

    public DatabaseOpener() {
    }

    @NotNull
    private List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return List.of(
                new FileChooser.ExtensionFilter("LibraryApp database files", "*." + System.getProperty("libraryapp.file.extension")),
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
                    .map(DatabaseMeta::new)
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
            return new DatabaseMeta(file);
        }

        return null;
    }
}
