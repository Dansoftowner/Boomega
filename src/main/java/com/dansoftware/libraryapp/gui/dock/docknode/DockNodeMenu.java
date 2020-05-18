package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.util.menu.MenuBuilder;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.Objects;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWords;

public class DockNodeMenu extends ContextMenu {

    private final DockTitleBar titleBar;

    public DockNodeMenu(DockTitleBar dockTitleBar) {
        this.titleBar = Objects.requireNonNull(dockTitleBar, "The dockTitleBar mustn't be null");
        createDefaultMenuItems();
    }

    public DockNodeMenu(DockTitleBar dockTitleBar, ObservableList<MenuItem> customMenuItems) {
        this(dockTitleBar);
        this.addCustomMenuItems(customMenuItems);
    }

    public void addCustomMenuItems(ObservableList<MenuItem> customMenuItems) {
        this.getItems().addAll(0, customMenuItems);
        this.getItems().add(customMenuItems.size(), new SeparatorMenuItem());
    }

    private void createDefaultMenuItems() {
        this.getItems().add(
                new MenuBuilder()
                        //.text(getGeneralWords().getString())
                        //.menuItem()
                        .build()
        );
    }


}
