package com.dansoftware.libraryapp.gui.info.dependency;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.controlsfx.control.HyperlinkLabel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class DependencyTable extends TableView<DependencyInfo> {

    public DependencyTable(@NotNull List<DependencyInfo> dependencies) {


    }

    private static final class NameColumn
            extends TableColumn<DependencyInfo, String>
            implements Callback<TableColumn<DependencyInfo, String>, TableCell<DependencyInfo, String>> {
        NameColumn() {
            setCellFactory(this);
        }

        @Override
        public TableCell<DependencyInfo, String> call(TableColumn<DependencyInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        DependencyInfo dependencyInfo = getTableRow().getItem();
                        Hyperlink hyperlink = new Hyperlink(dependencyInfo.getName());
                        hyperlink.setOnAction(event -> {
                            try {
                                Desktop.getDesktop().browse(URI.create(dependencyInfo.getWebsiteUrl()));
                            } catch (IOException e) {

                            }
                        });

                        setGraphic(new Hyperlink());
                    }
                }
            };
        }
    }
}
