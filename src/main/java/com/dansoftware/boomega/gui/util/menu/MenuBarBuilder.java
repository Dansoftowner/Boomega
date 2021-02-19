package com.dansoftware.boomega.gui.util.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MenuBarBuilder {
    private ObservableList<Menu> menus;

    public MenuBarBuilder() {
        this.menus = FXCollections.observableArrayList();
    }

    public MenuBarBuilder menu(Menu menu) {
        menus.add(menu);
        return this;
    }

    public MenuBar build() {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menus);

        menus = null;
        return menuBar;
    }

}
