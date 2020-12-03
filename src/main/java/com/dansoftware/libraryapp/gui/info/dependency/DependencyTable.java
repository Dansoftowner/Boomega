package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.info.dependency.meta.DependencyInfo;
import com.dansoftware.libraryapp.gui.info.dependency.meta.LicenseInfo;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
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
        implements Themeable, ContextTransformable {

    private static final Logger logger = LoggerFactory.getLogger(DependencyTable.class);

    public DependencyTable(@NotNull List<DependencyInfo> dependencies) {
        getColumns().add(new NameColumn());
        getColumns().add(new LicenseColumn());
        getItems().addAll(dependencies);
        Theme.registerThemeable(this);
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this);
        newTheme.apply(this);
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
                        dependencyInfo.getWebsiteUrl().ifPresentOrElse(
                                website -> setGraphic(new WebsiteHyperLink(dependencyInfo.getName(), website)),
                                () -> {
                                    setGraphic(null);
                                    setText(dependencyInfo.getName());
                                }
                        );
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
                        licenseInfo.getWebsiteUrl().ifPresentOrElse(
                                website -> setGraphic(new WebsiteHyperLink(licenseInfo.getName(), website)),
                                () -> {
                                    setGraphic(null);
                                    setText(licenseInfo.getName());
                                }
                        );
                    }
                }
            };
        }
    }
}
