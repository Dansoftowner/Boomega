package com.dansoftware.boomega.gui.pluginmngr;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.util.FXCollectionUtils;
import com.dansoftware.boomega.gui.util.I18NButtonTypes;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.plugin.PluginDirectory;
import com.dansoftware.boomega.util.FileIOException;
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
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link TableView} for displaying the list of plugin files to the user.
 *
 * @author Daniel Gyorffy
 */
public class PluginTable extends TableView<File> {

    private static final Logger logger = LoggerFactory.getLogger(PluginTable.class);

    private final Context context;

    public PluginTable(@NotNull Context context, @NotNull ObservableList<File> pluginFiles) {
        this.context = context;
        setItems(pluginFiles);
        getColumns().addAll(List.of(new NameColumn(), new SizeColumn(), new DeleteColumn()));
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private static final class NameColumn extends TableColumn<File, String>
            implements Callback<TableColumn<File, String>, TableCell<File, String>> {
        NameColumn() {
            super(I18N.getValues().getString("plugin.module.list.column.name"));
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
            super(I18N.getValue("plugin.module.list.column.size"));
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
            super(I18N.getValues().getString("plugin.module.list.column.delete"));
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
                        try {
                            deleteButton.disableProperty().bind(getTableRow().selectedProperty().not());
                        } catch (NullPointerException ignored) {
                        }
                        setGraphic(deleteButton);
                    }
                }
            };
        }
    }

    private final class PluginDeleteDialog {

        public void show(@NotNull ObservableList<File> itemsToRemove) {
            PluginTable.this.context.showDialog(
                    I18N.getValue("plugin.delete.title", itemsToRemove.size()),
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
                                        I18N.getValue("plugin.delete.failed.title"),
                                        I18N.getValue("plugin.delete.failed.msg", e.getFile()), e
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