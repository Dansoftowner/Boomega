package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DependencyTable extends TableView<DependencyInfo> {

    private static final Logger logger = LoggerFactory.getLogger(DependencyTable.class);

    public DependencyTable(@NotNull List<DependencyInfo> dependencies) {
        getColumns().add(new NameColumn());
        getColumns().add(new LicenseColumn());
        getItems().addAll(dependencies);
    }

    private static final class NameColumn
            extends TableColumn<DependencyInfo, String>
            implements Callback<TableColumn<DependencyInfo, String>, TableCell<DependencyInfo, String>> {
        NameColumn() {
            setText("i18n Software");
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
                        DependencyInfo dependencyInfo = getTableView().getItems().get(getIndex());
                        setGraphic(new WebsiteHyperLink(dependencyInfo.getName(), dependencyInfo.getWebsiteUrl()));
                    }
                }
            };
        }
    }

    private static final class LicenseColumn
            extends TableColumn<DependencyInfo, String>
            implements Callback<TableColumn<DependencyInfo, String>, TableCell<DependencyInfo, String>> {

        LicenseColumn() {
            setText("License");
            setCellFactory(this);
        }

        @Override
        public TableCell<DependencyInfo, String> call(TableColumn<DependencyInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        DependencyInfo dependencyInfo = getTableView().getItems().get(getIndex());
                        LicenseInfo licenseInfo = dependencyInfo.getLicenseInfo();
                        setGraphic(new WebsiteHyperLink(licenseInfo.getName(), licenseInfo.getWebsiteUrl()));
                    }
                }
            };
        }
    }
}
