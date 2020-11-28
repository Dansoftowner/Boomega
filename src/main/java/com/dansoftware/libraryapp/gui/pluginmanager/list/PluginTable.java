package com.dansoftware.libraryapp.gui.pluginmanager.list;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.FXCollectionUtils;
import com.dansoftware.libraryapp.gui.util.I18NButtonTypes;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.plugin.PluginDirectory;
import com.dansoftware.libraryapp.util.FileIOException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PluginTable extends TableView<File> implements Themeable {

    private static final Logger logger = LoggerFactory.getLogger(PluginTable.class);

    private final Context context;

    PluginTable(@NotNull Context context, @NotNull List<File> pluginFiles) {
        this.context = context;
        getItems().addAll(pluginFiles);
        getColumns().addAll(List.of(new NameColumn(), new SizeColumn(), new DeleteColumn()));
        Theme.registerThemeable(this);
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.getCustomApplier().applyBack(this);
        newTheme.getCustomApplier().apply(this);
    }

    private static final class NameColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {
        NameColumn() {
            super(I18N.getPluginManagerValues().getString("plugin.module.list.column.name"));
            setCellFactory(this);
        }

        @Override
        public TableCell<File, String> call(TableColumn<File, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        File pluginFile = getTableView().getItems().get(getIndex());
                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.COFFEE));
                        setText(FilenameUtils.getBaseName(pluginFile.getPath()));
                    }
                }
            };
        }
    }

    private static final class SizeColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {
        SizeColumn() {
            super(I18N.getPluginManagerValue("plugin.module.list.column.size"));
            setCellFactory(this);
        }

        @Override
        public TableCell<File, String> call(TableColumn<File, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        File file = getTableView().getItems().get(getIndex());
                        setText(FileUtils.byteCountToDisplaySize(file.length()));
                    }
                }
            };
        }
    }

    private final class DeleteColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {
        DeleteColumn() {
            super(I18N.getPluginManagerValues().getString("plugin.module.list.column.delete"));
            setCellFactory(this);
        }


        @Override
        public TableCell<File, String> call(TableColumn<File, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button deleteButton = new Button();
                        deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        deleteButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE));
                        deleteButton.setOnAction(e -> {
                            var dialog = new PluginDeleteDialog();
                            dialog.show(FXCollectionUtils.copyOf(getTableView().getSelectionModel().getSelectedItems()));
                        });
                        setGraphic(deleteButton);
                    }
                }
            };
        }
    }

    private final class PluginDeleteDialog {

        public void show(@NotNull ObservableList<File> itemsToRemove) {
            PluginTable.this.context.showDialog(
                    I18N.getPluginManagerValue("plugin.delete.title", itemsToRemove.size()),
                    new ListView<>(itemsToRemove.stream()
                            .map(File::getName)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList))),
                    buttonType -> {
                        if (Objects.equals(buttonType, I18NButtonTypes.YES)) {
                            try {
                                unregisterPlugins(itemsToRemove, PluginTable.this.getItems()::remove);
                            } catch (FileIOException e) {
                                logger.error("Couldn't delete plugin file", e);
                                context.showErrorDialog(
                                        I18N.getPluginManagerValue("plugin.delete.failed.title"),
                                        I18N.getPluginManagerValue("plugin.delete.failed.msg", e.getFile()), e
                                );
                            }
                        }
                    },
                    I18NButtonTypes.YES,
                    I18NButtonTypes.NO
            );
        }

        private void unregisterPlugins(List<File> files, Consumer<File> onFileDeleted) throws FileIOException {
            for (File file : files) {
                try {
                    PluginDirectory.INSTANCE.unregisterPlugin(file);
                    onFileDeleted.accept(file);
                } catch (Exception e) {
                    throw new FileIOException(file, e);
                }
            }
        }

    }
}
