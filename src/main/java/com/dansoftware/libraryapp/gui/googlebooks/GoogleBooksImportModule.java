package com.dansoftware.libraryapp.gui.googlebooks;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.ABCCollators;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.SystemBrowser;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;
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

    private static final Preferences.Key<Locale> abcConfigKey =
            new Preferences.Key<>(
                    "google.books.module.table.abcsort",
                    Locale.class,
                    Locale::getDefault
            );

    private final Context context;
    private final Preferences preferences;
    private final ObjectProperty<GoogleBooksImportView> content =
            new SimpleObjectProperty<>();

    private ToolbarItem columnChooserItem;
    private ToolbarItem abcChooserItem;

    private final ObjectProperty<Locale> abcLocale =
            new SimpleObjectProperty<>();

    public GoogleBooksImportModule(@NotNull Context context,
                                   @NotNull Preferences preferences) {
        super(I18N.getGoogleBooksImportValue("google.books.import.module.title"), MaterialDesignIcon.GOOGLE);
        this.context = context;
        this.preferences = preferences;
        this.buildTableConfiguration();
        this.buildToolbar();
    }

    @Override
    public Node activate() {
        if (content.get() == null)
            content.set(new GoogleBooksImportView(context));
        return content.get();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean destroy() {
        preferences.editor().put(colConfigKey, new TableColumnsInfo(getTable().getShowingColumns()));
        preferences.editor().put(abcConfigKey, abcLocale.get());
        content.set(null);
        return true;
    }

    private void buildTableConfiguration() {
        content.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GoogleBooksTable table = newValue.getTable();
                readColumnConfigurations(table);
                readABCConfigurations();
            }
        });
    }

    private void readColumnConfigurations(GoogleBooksTable table) {
        List<GoogleBooksTable.ColumnType> columnTypes =
                preferences.get(colConfigKey).columnTypes;
        columnTypes.forEach(table::addColumn);
        columnChooserItem.getItems().stream()
                .map(menuItem -> (TableColumnMenuItem) menuItem)
                .forEach(menuItem -> menuItem.setSelected(table.isColumnShown(menuItem.columnType)));
    }

    private void readABCConfigurations() {
        final Locale locale = preferences.get(abcConfigKey);
        abcChooserItem.getItems().stream()
                .map(item -> (AbcMenuItem) item)
                .filter(item -> locale.equals(item.locale))
                .forEach(item -> item.setSelected(true));
    }

    private void buildToolbar() {
        this.getToolbarControlsRight().add(buildRefreshItem());
        this.getToolbarControlsRight().add(buildScrollToTopItem());
        this.getToolbarControlsRight().add(buildClearItem());
        this.getToolbarControlsRight().add(buildBrowserItem());

        this.getToolbarControlsLeft().add(buildGoogleLogoItem());
        this.getToolbarControlsLeft().add(columnChooserItem = buildColumnChooserItem());
        this.getToolbarControlsLeft().add(buildColumnResetItem());
        this.getToolbarControlsLeft().add(abcChooserItem = buildABCChooserItem());
    }

    private ToolbarItem buildGoogleLogoItem() {
        return new ToolbarItem(
                new StackPane(new Group(new HBox(5,
                        new ImageView("/com/dansoftware/libraryapp/image/util/google_24px.png"),
                        new Label("Google Books") {{ setFont(Font.font(20)); }}
                )))
        );
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

    private ToolbarItem buildABCChooserItem() {
        var toolbarItem = new ToolbarItem(I18N.getGoogleBooksImportValue("google.books.abc"));
        var toggleGroup = new ToggleGroup();
        ABCCollators.getAvailableCollators().forEach((locale, collatorSupplier) -> {
            toolbarItem.getItems().add(new AbcMenuItem(locale, collatorSupplier, toggleGroup));
        });
        return toolbarItem;
    }

    private ToolbarItem buildToolbarItem(MaterialDesignIcon icon, String i18nTooltip, EventHandler<MouseEvent> onClick) {
        var toolbarItem = new ToolbarItem(new MaterialDesignIconView(icon), onClick);
        toolbarItem.setTooltip(new Tooltip(I18N.getGoogleBooksImportValue(i18nTooltip)));
        return toolbarItem;
    }

    private GoogleBooksSearchView getContent() {
        return content.get();
    }

    private GoogleBooksTable getTable() {
        return getContent().getTable();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private final class AbcMenuItem extends RadioMenuItem {
        private final Locale locale;

        AbcMenuItem(@NotNull Locale locale,
                    @NotNull Supplier<Collator> collatorSupplier,
                    @NotNull ToggleGroup toggleGroup) {
            super(locale.getDisplayLanguage(), new MaterialDesignIconView(MaterialDesignIcon.TRANSLATE));
            this.locale = locale;
            this.setUserData(locale);
            selectedProperty().addListener((observable, oldValue, selected) -> {
                if (selected) {
                    getTable().setSortingComparator((Comparator) collatorSupplier.get());
                    GoogleBooksImportModule.this.abcLocale.set(locale);
                }
            });
            setToggleGroup(toggleGroup);
        }
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
