package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.SystemBrowser;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link GoogleBooksImportModule} is a module that allows to search books
 * through the Google Books service and also allows to import books and add them into
 * the local database.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBooksImportModule extends WorkbenchModule {

    private static final Preferences.Key<TableColumnsInfo> colConfigKey =
            new Preferences.Key<>("google.books.module.table.columns", TableColumnsInfo.class, TableColumnsInfo::byDefault);

    private final Context context;
    private final Preferences preferences;
    private final Database database;
    private final ObjectProperty<GoogleBooksImportPanel> content =
            new SimpleObjectProperty<>();

    private ToolbarItem columnChooserItem;

    public GoogleBooksImportModule(@NotNull Context context,
                                   @NotNull Preferences preferences,
                                   @NotNull Database database) {
        super(I18N.getGoogleBooksImportValue("google.books.import.module.title"), MaterialDesignIcon.GOOGLE);
        this.context = context;
        this.database = database;
        this.preferences = preferences;
    }

    @Override
    public void init(Workbench workbench) {
        super.init(workbench);
        this.buildTableConfiguration();
        this.buildToolbar();
    }

    @Override
    public Node activate() {
        if (content.get() == null)
            content.set(new GoogleBooksImportPanel(context, database));
        return content.get();
    }

    @Override
    public boolean destroy() {
        preferences.editor().put(colConfigKey, new TableColumnsInfo(getTable().getShowingColumns()));
        content.set(null);
        return true;
    }

    private void buildTableConfiguration() {
        content.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GoogleBooksTable table = newValue.getTable();
                List<GoogleBooksTable.ColumnType> columnTypes =
                        preferences.get(colConfigKey).columnTypes;
                columnTypes.forEach(table::addColumn);
                columnChooserItem.getItems().stream()
                        .map(menuItem -> (TableColumnMenuItem) menuItem)
                        .forEach(menuItem -> menuItem.setSelected(table.isColumnShown(menuItem.columnType)));
            }
        });
    }

    private void buildToolbar() {
        this.getToolbarControlsRight().add(buildRefreshItem());
        this.getToolbarControlsRight().add(buildScrollToTopItem());
        this.getToolbarControlsRight().add(buildClearItem());
        this.getToolbarControlsRight().add(buildBrowserItem());

        this.getToolbarControlsLeft().add(columnChooserItem = buildColumnChooserItem());
        this.getToolbarControlsLeft().add(buildColumnResetItem());
    }

    private ToolbarItem buildRefreshItem() {
        return buildToolbarItem(MaterialDesignIcon.REFRESH, "google.books.toolbar.refresh", event -> getContent().refresh());
    }

    private ToolbarItem buildScrollToTopItem() {
        return buildToolbarItem(MaterialDesignIcon.BORDER_TOP, "google.books.toolbar.scrolltop", event -> getContent().scrollToTop());
    }

    private ToolbarItem buildBrowserItem() {
        return buildToolbarItem(MaterialDesignIcon.GOOGLE_CHROME, "google.books.toolbar.website", event -> {
            if (SystemBrowser.isSupported())
                new SystemBrowser().browse("https://books.google.com/advanced_book_search");
        });
    }

    private ToolbarItem buildClearItem() {
        return buildToolbarItem(MaterialDesignIcon.DELETE, "google.books.toolbar.clear", event -> getContent().clear());
    }

    private ToolbarItem buildColumnChooserItem() {
        var toolbarItem = new ToolbarItem(
                I18N.getGoogleBooksImportValue("google.books.toolbar.columns"),
                new FontAwesomeIconView(FontAwesomeIcon.COLUMNS));
        Stream.of(GoogleBooksTable.ColumnType.values())
                .map(TableColumnMenuItem::new)
                .forEach(toolbarItem.getItems()::add);
        return toolbarItem;
    }

    private ToolbarItem buildColumnResetItem() {
        return buildToolbarItem(MaterialDesignIcon.TABLE, "google.books.toolbar.colreset", event -> {
            getTable().buildDefaultColumns();
            columnChooserItem.getItems().stream()
                    .map(item -> (TableColumnMenuItem) item)
                    .forEach(item -> item.setSelected(item.columnType.isDefaultVisible()));
        });
    }

    private ToolbarItem buildToolbarItem(MaterialDesignIcon icon, String i18nTooltip, EventHandler<MouseEvent> onClick) {
        var toolbarItem = new ToolbarItem(new MaterialDesignIconView(icon), onClick);
        toolbarItem.setTooltip(new Tooltip(I18N.getGoogleBooksImportValue(i18nTooltip)));
        return toolbarItem;
    }

    private GoogleBooksImportPanel getContent() {
        return content.get();
    }

    private GoogleBooksTable getTable() {
        return getContent().getTable();
    }

    private final class TableColumnMenuItem extends CheckMenuItem {

        private final GoogleBooksTable.ColumnType columnType;

        TableColumnMenuItem(GoogleBooksTable.ColumnType columnType) {
            super(I18N.getGoogleBooksImportValue(columnType.getI18Nkey()));
            this.columnType = columnType;
            this.setOnAction(e -> {
                if (!this.isSelected()) {
                    getTable().removeColumn(columnType);
                } else {
                    getTable().removeAllColumns();
                    columnChooserItem.getItems().stream()
                            .map(menuItem -> (TableColumnMenuItem) menuItem)
                            .filter(CheckMenuItem::isSelected)
                            .map(menuItem -> menuItem.columnType)
                            .forEach(colType -> getTable().addColumn(colType));
                }
            });
        }
    }

    /**
     * Used for storing the preferred table columns in the configurations.
     */
    static class TableColumnsInfo {

        private final List<GoogleBooksTable.ColumnType> columnTypes;

        TableColumnsInfo(@NotNull List<GoogleBooksTable.ColumnType> columnTypes) {
            this.columnTypes = Objects.requireNonNull(columnTypes);
        }

        public static TableColumnsInfo byDefault() {
            return new TableColumnsInfo(
                    Arrays.stream(GoogleBooksTable.ColumnType.values())
                            .filter(GoogleBooksTable.ColumnType::isDefaultVisible)
                            .collect(Collectors.toList())
            );
        }
    }
}
