package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.util.menu.MenuBuilder;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.Objects;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWords;

public class DockNodeMenu extends ContextMenu {

    private final DockNode dockNode;

    public DockNodeMenu(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockTitleBar mustn't be null");
        createDefaultMenuItems();
    }

    private void createDefaultMenuItems() {
        this.getItems().add(
                new MenuBuilder()
                        //.text(getGeneralWords().getString())
                        //.menuItem()
                        .build()
        );
    }

    public void addCustomMenuItems(ObservableList<MenuItem> customMenuItems) {
        this.getItems().addAll(0, customMenuItems);
        this.getItems().add(customMenuItems.size(), new SeparatorMenuItem());
    }

}
