package com.dansoftware.boomega.gui.info.dependency;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.control.WebsiteHyperLink;
import com.dansoftware.boomega.gui.info.dependency.meta.DependencyInfo;
import com.dansoftware.boomega.gui.info.dependency.meta.LicenseInfo;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A DependencyTable is a {@link TableView} that can show the third party software
 * used by this app.
 *
 * @link Daniel Gyorffy
 */
public class DependencyTable extends TableView<DependencyInfo>
        implements ContextTransformable {

    private static final Logger logger = LoggerFactory.getLogger(DependencyTable.class);

    public DependencyTable(@NotNull List<DependencyInfo> dependencies) {
        getColumns().add(new NameColumn());
        getColumns().add(new LicenseColumn());
        getItems().addAll(dependencies);
    }

    @Override
    public @NotNull Context getContext() {
        return Context.empty();
    }

    /**
     * A {@link TableColumn} implementation that shows the name of the dependency.
     */
    private static final class NameColumn
            extends TableColumn<DependencyInfo, String>
            implements Callback<TableColumn<DependencyInfo, String>, TableCell<DependencyInfo, String>> {
        NameColumn() {
            setText("Software");
            setCellFactory(this);
            setSortable(false);
            setMinWidth(300);
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
                        dependencyInfo.getWebsiteUrl()
                                .map(it -> new WebsiteHyperLink(dependencyInfo.getName(), it))
                                .ifPresentOrElse(value -> {
                                    setGraphic(value);
                                    setText(null);
                                }, () -> {
                                    setGraphic(null);
                                    setText(dependencyInfo.getName());
                                });
                    }
                }
            };
        }
    }

    /**
     * A {@link TableColumn} implementation that shows the license of the depencency.
     */
    private static final class LicenseColumn
            extends TableColumn<DependencyInfo, String>
            implements Callback<TableColumn<DependencyInfo, String>, TableCell<DependencyInfo, String>> {

        LicenseColumn() {
            setText("License");
            setCellFactory(this);
            setSortable(false);
            setMinWidth(300);
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
                        licenseInfo.getWebsiteUrl()
                                .map(it -> new WebsiteHyperLink(licenseInfo.getName(), it))
                                .ifPresentOrElse(value -> {
                                    setGraphic(value);
                                    setText(null);
                                }, () -> {
                                    this.setGraphic(null);
                                    setText(licenseInfo.getName());
                                });
                    }
                }
            };
        }
    }
}
