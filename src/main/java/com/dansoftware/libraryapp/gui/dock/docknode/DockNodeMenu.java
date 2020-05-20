package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.ViewMode;
import com.dansoftware.libraryapp.gui.util.menu.MenuBuilder;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;

import java.util.Objects;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

public class DockNodeMenu extends ContextMenu {

    private final DockNode dockNode;

    public DockNodeMenu(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockTitleBar mustn't be null");
        this.getStyleClass().add("dock-options-menu");
        createDefaultMenuItems();
    }

    private void createDefaultMenuItems() {
        this.getItems().addAll(
                createViewModeMenu(),
                createPositionMenu()
        );
    }

    private Menu createPositionMenu() {
        var menuBuilder = new MenuBuilder()
                .text(getGeneralWord(DockPosition.NAME_LOCALE_KEY));

        for (DockPosition pos : DockPosition.values()) {
            var graphic = new ImageView();
            graphic.getStyleClass().add(pos.getId());
            var actualItem = new MenuItem(getGeneralWord(pos.getLocaleKey()), graphic);
            actualItem.setOnAction(e -> this.dockNode.setDockPosition(pos));
            menuBuilder.menuItem(actualItem);
        }

        return menuBuilder.build();
    }

    private Menu createViewModeMenu() {
        var menuBuilder = new MenuBuilder()
                .text(getGeneralWord(ViewMode.NAME_LOCALE_KEY));

        for (ViewMode mode : ViewMode.values()) {
            var graphic = new ImageView();
            graphic.getStyleClass().add(mode.getId());
            var actualItem = new MenuItem(getGeneralWord(mode.getLocaleKey()), graphic);
            actualItem.setOnAction(event -> this.dockNode.setViewMode(mode));
            menuBuilder.menuItem(actualItem);
        }

        return menuBuilder.build();
    }

    public void addCustomMenuItems(ObservableList<MenuItem> customMenuItems) {
        this.getItems().addAll(0, customMenuItems);
        this.getItems().add(customMenuItems.size(), new SeparatorMenuItem());
    }

}
