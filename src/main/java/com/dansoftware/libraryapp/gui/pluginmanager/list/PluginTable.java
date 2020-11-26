package com.dansoftware.libraryapp.gui.pluginmanager.list;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.FXCollectionUtils;
import com.dansoftware.libraryapp.gui.util.I18NButtonTypes;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.FileIOException;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class PluginTable extends TableView<File> {

    private final Context context;

    PluginTable(@NotNull Context context, @NotNull List<File> pluginFiles) {
        this.context = context;
        getItems().addAll(pluginFiles);
        getColumns().addAll(new NameColumn(), new PathColumn(), new DeleteColumn());
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

    private static final class PathColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {

        PathColumn() {
            super(I18N.getPluginManagerValues().getString("plugin.module.list.column.path"));
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
                        File pluginFile = getTableView().getItems().get(getIndex());
                        setText(pluginFile.getPath());
                    }
                }
            };
        }
    }

    private final class DeleteColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {
        DeleteColumn() {
            super(I18N.getPluginManagerValues().getString("plugin.module.list.column.delete"));
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
                    }
                }
            };
        }
    }

    private final class PluginDeleteDialog {

        public void show(@NotNull ObservableList<File> itemsToRemove) {
            PluginTable.this.context.showDialog(
                    I18N.getPluginManagerValue("plugin.delete.title", itemsToRemove.size()),
                    new ListView<>(itemsToRemove),
                    buttonType -> {
                        if (Objects.equals(buttonType, I18NButtonTypes.YES)) {
                            try {
                                deleteFiles(itemsToRemove);
                            } catch (FileIOException e) {
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

        private void deleteFiles(List<File> files) throws FileIOException {
            for (File file : files) {
                try {
                    Files.delete(file.toPath());
                    PluginTable.this.getItems().remove(file);
                } catch (IOException e) {
                    throw new FileIOException(file, e);
                }
            }
        }

    }
}
