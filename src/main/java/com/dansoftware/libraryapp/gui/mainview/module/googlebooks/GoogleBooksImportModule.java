package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.SystemBrowser;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

public class GoogleBooksImportModule extends WorkbenchModule {

    private final Context context;
    private final Database database;
    private GoogleBooksImportPanel content;

    public GoogleBooksImportModule(@NotNull Context context, @NotNull Database database) {
        super(I18N.getGoogleBooksImportValue("google.books.import.module.title"), MaterialDesignIcon.GOOGLE);
        this.context = context;
        this.database = database;
        this.buildToolbar();
    }

    private void buildToolbar() {
        this.getToolbarControlsRight().add(buildRefreshItem());
        this.getToolbarControlsRight().add(buildScrollToTopItem());
        this.getToolbarControlsRight().add(buildClearItem());
        this.getToolbarControlsRight().add(buildBrowserItem());
    }

    private ToolbarItem buildRefreshItem() {
        return buildToolbarItem(MaterialDesignIcon.REFRESH, "google.books.toolbar.refresh", event -> this.content.refresh());
    }

    private ToolbarItem buildScrollToTopItem() {
        return buildToolbarItem(MaterialDesignIcon.BORDER_TOP, "google.books.toolbar.scrolltop", event -> content.scrollToTop());
    }

    private ToolbarItem buildBrowserItem() {
        return buildToolbarItem(MaterialDesignIcon.GOOGLE_CHROME, "google.books.toolbar.website", event -> {
            if (SystemBrowser.isSupported())
                new SystemBrowser().browse("https://books.google.com/advanced_book_search");
        });
    }

    private ToolbarItem buildClearItem() {
        return buildToolbarItem(MaterialDesignIcon.DELETE, "google.books.toolbar.clear", event -> {
            if (SystemBrowser.isSupported())
                new SystemBrowser().browse("https://books.google.com/advanced_book_search");
        });
    }

    private ToolbarItem buildToolbarItem(MaterialDesignIcon icon, String i18nTooltip, EventHandler<MouseEvent> onClick) {
        var toolbarItem = new ToolbarItem(new MaterialDesignIconView(icon), onClick);
        toolbarItem.setTooltip(new Tooltip(I18N.getGoogleBooksImportValue(i18nTooltip)));
        return toolbarItem;
    }

    @Override
    public Node activate() {
        if (content == null) {
            content = new GoogleBooksImportPanel(context, database);
        }
        return content;
    }

    @Override
    public boolean destroy() {
        content = null;
        return true;
    }
}
