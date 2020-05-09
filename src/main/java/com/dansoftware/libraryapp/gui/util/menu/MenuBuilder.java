package com.dansoftware.libraryapp.gui.util.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuBuilder {

    private String text;
    private ObservableList<MenuItem> menuItems;

    public MenuBuilder() {
        this.menuItems = FXCollections.observableArrayList();
    }

    public MenuBuilder menuItem(MenuItem item) {
        menuItems.add(item);
        return this;
    }

    public MenuBuilder text(String text) {
        this.text = text;
        return this;
    }

    public Menu build() {
        Menu menu = new Menu(text);
        menu.getItems().addAll(menuItems);

        menuItems = null;
        return menu;
    }

}
