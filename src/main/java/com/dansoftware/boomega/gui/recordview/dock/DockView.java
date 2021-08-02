/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.recordview.dock;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import static com.dansoftware.boomega.gui.util.Icons.icon;

public abstract class DockView<T extends Node> extends VBox {

    private final T content;
    private final HBox buttonBar;

    public DockView(@NotNull SplitPane parent,
                    @NotNull Node icon,
                    @NotNull String title,
                    @NotNull T content) {
        this.content = content;
        this.buttonBar = buildButtonBar(parent);
        this.buildUI(parent, icon, title);
    }

    private void buildUI(SplitPane parent, Node icon, String title) {
        SplitPane.setResizableWithParent(this, false);
        VBox.setVgrow(content, Priority.ALWAYS);
        getStyleClass().add("titled-dock");
        getChildren().add(buildTitleBar(parent, icon, title));
        getChildren().add(content);
    }

    private Node buildTitleBar(SplitPane parent, Node icon, String title) {
        var iconTitleBar = new HBox(new StackPane(icon), new StackPane(new Label(title)));
        iconTitleBar.getStyleClass().add("h-box");
        var titleBar = new BorderPane(null, null, buttonBar, null, iconTitleBar);
        titleBar.getStyleClass().add("dock-title-bar");
        return titleBar;
    }

    private HBox buildButtonBar(SplitPane parent) {
        return new HBox(5.0, buildLeftButton(parent), buildRightButton(parent));
    }

    private Button buildLeftButton(SplitPane parent) {
        Button btn = buildRearrangeButton("arrow-left-icon");
        parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
            btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == 0);
        });
        btn.setOnAction(e -> {
            int index = parent.getItems().indexOf(this);
            parent.getItems().remove(this);
            parent.getItems().add(index - 1, this);
        });
        return btn;
    }

    private Button buildRightButton(SplitPane parent) {
        Button btn = buildRearrangeButton("arrow-right-icon");
        parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
            btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == parent.getItems().size() - 1);
        });
        btn.setOnAction(e -> {
            int index = parent.getItems().indexOf(this);
            parent.getItems().remove(this);
            parent.getItems().add(index + 1, this);
        });
        return btn;
    }

    private Button buildRearrangeButton(String iconStyleClass) {
        var btn = new Button(null, icon(iconStyleClass));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setPadding(new Insets(0));
        return btn;
    }

    protected T getContent() {
        return content;
    }

    protected HBox getButtonBar() {
        return buttonBar;
    }
}
