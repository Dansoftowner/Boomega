package com.dansoftware.boomega.gui.record.show.dock;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

public abstract class DockView<T extends Node> extends VBox {
    private final T content;

    public DockView(@NotNull SplitPane parent,
               @NotNull Node icon,
               @NotNull String title,
               @NotNull T content) {
        this.content = content;
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
        var buttonBar = new HBox(5.0, buildUpButton(parent), buildDownButton(parent));
        var iconTitleBar = new HBox(10.0, new StackPane(icon), new StackPane(new Label(title)));
        var titleBar = new BorderPane(null, null, buttonBar, null, iconTitleBar);
        titleBar.getStyleClass().add("dock-title-bar");
        return titleBar;
    }

    private Button buildUpButton(SplitPane parent) {
        Button btn = buildRearrangeButton(MaterialDesignIcon.ARROW_UP);
        parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
            btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == 0);
        });
        btn.setOnAction(e -> {
            int index = parent.getItems().indexOf(this);
            parent.getItems().remove(this);
            parent.getItems().add(index -1, this);
        });
        return btn;
    }

    private Button buildDownButton(SplitPane parent) {
        Button btn = buildRearrangeButton(MaterialDesignIcon.ARROW_DOWN);
        parent.getItems().addListener((ListChangeListener<? super Node>) modification -> {
            btn.setDisable(parent.getItems().size() < 2 || parent.getItems().indexOf(this) == parent.getItems().size() -1);
        });
        btn.setOnAction(e -> {
            int index = parent.getItems().indexOf(this);
            parent.getItems().remove(this);
            parent.getItems().add(index + 1, this);
        });
        return btn;
    }

    private Button buildRearrangeButton(MaterialDesignIcon icon) {
        var btn = new Button(null, new MaterialDesignIconView(icon));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setPadding(new Insets(0));
        return btn;
    }
}
