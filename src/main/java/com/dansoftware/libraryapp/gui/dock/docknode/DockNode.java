package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.border.BorderButton;
import com.dansoftware.libraryapp.gui.dock.docksystem.DockSystem;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DockNode extends BorderPane {

    private static final String STYLE_CLASS_NAME = "dock-node";

    private Stage stage;

    //
    private DockSystem<?> dockSystem;
    private DockPosition dockPosition;

    //
    private final DockTitleBar dockTitleBar;
    private final BorderButton borderButton;

    //
    private ViewMode viewMode = ViewMode.PINNED;

    //
    private final StringProperty title = new SimpleStringProperty(this, "name");
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");
    private final BooleanProperty showing = new SimpleBooleanProperty(this, "showing");

    public DockNode(String title) {
        this.setTop(this.dockTitleBar = new DockTitleBar(this));
        this.borderButton = new BorderButton();
        this.borderButton.graphicProperty().bind(this.graphic);

        this.title.addListener((observable, oldName, newName) -> {
            this.borderButton.setText(newName);
            this.dockTitleBar.setTitle(newName);
        });

        this.title.set(title);

        this.getStyleClass().add(STYLE_CLASS_NAME);
    }

    public DockNode(Node graphic, String title) {
        this(title);
        this.setGraphic(graphic);
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public void show() {
        switch (this.viewMode) {

            case PINNED:
                this.dockSystem.dock(this.dockPosition, this);
                break;
            case FLOAT:
                break;
            case WINDOW:
                break;
        }

        this.showing.set(true);
    }

    public void hide() {
        switch (this.viewMode) {

            case PINNED:
                this.dockSystem.hide(this.dockPosition, this);
                break;
            case FLOAT:
                break;
            case WINDOW:
                break;
        }

        this.showing.set(false);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public boolean isShowing() {
        return showing.get();
    }

    public BooleanProperty showingProperty() {
        return showing;
    }

    public DockTitleBar getDockTitleBar() {
        return dockTitleBar;
    }

    public DockSystem<?> getDockSystem() {
        return dockSystem;
    }

    public DockPosition getDockPosition() {
        return dockPosition;
    }

    public void setDockPosition(DockPosition dockPosition) {
        this.dockPosition = dockPosition;
    }

    public void setDockSystem(DockSystem<?> dockSystem) {
        this.dockSystem = dockSystem;
    }

    public BorderButton getBorderButton() {
        return borderButton;
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }

    public Node getGraphic() {
        return graphic.get();
    }

    public ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public enum ViewMode {
        PINNED, FLOAT, WINDOW
    }
}
