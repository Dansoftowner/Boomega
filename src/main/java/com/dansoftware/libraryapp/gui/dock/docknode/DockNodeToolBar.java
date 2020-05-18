package com.dansoftware.libraryapp.gui.dock.docknode;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

import java.util.Objects;

public class DockNodeToolBar extends ToolBar {

    private final DockTitleBar titleBar;
    private final DockNodeMenu dockNodeMenu;

    public DockNodeToolBar(DockTitleBar titleBar, DockNodeMenu dockNodeMenu) {
        this.titleBar = Objects.requireNonNull(titleBar, "The titleBar mustn't be null");
        this.dockNodeMenu = dockNodeMenu;
        this.createDefaultButtons();
    }

    public DockNodeToolBar(DockTitleBar titleBar, DockNodeMenu dockNodeMenu, ObservableList<Button> customButtons) {
        this(titleBar, dockNodeMenu);
        this.addCustomButtons(customButtons);
    }

    public void addCustomButtons(ObservableList<Button> customButtons) {
        this.getItems().addAll(0, customButtons);
        this.getItems().add(customButtons.size(), new Separator());
    }

    private void createDefaultButtons() {

    }
}
