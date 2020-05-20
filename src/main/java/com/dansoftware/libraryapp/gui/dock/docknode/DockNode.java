package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.ViewMode;
import com.dansoftware.libraryapp.gui.dock.border.BorderButton;
import com.dansoftware.libraryapp.gui.dock.docksystem.DockSystem;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class DockNode extends BorderPane {

    private static final String STYLE_CLASS_NAME = "dock-node";

    /**
     * The title-bar of this dock-node
     */
    private final DockTitleBar dockTitleBar;

    /**
     * The button on the border that can make this dockNode visible/invisible
     */
    private final BorderButton borderButton;

    /**
     * The menu that can give access to the user to set the position or
     * the view-mode of this dock-node dynamically
     */
    private final DockNodeMenu menu;

    /**
     * The observable-value that represents the dockSystem that owns this dockNode
     */
    private final SimpleObjectProperty<DockSystem<?>> dockSystem = new SimpleObjectProperty<>(this, "dockSystem");

    /**
     * The observable-value that represents the position of this dockNode
     */
    private final SimpleObjectProperty<DockPosition> dockPosition = new SimpleObjectProperty<>(this, "dockPosition");

    /**
     * The observable-value that represents the view-mode of this dockNode
     */
    private final SimpleObjectProperty<ViewMode> viewMode = new SimpleObjectProperty<>(this, "viewMode", ViewMode.PINNED);

    /***
     * The observable-value that represents the title of this dockNode
     */
    private final StringProperty title = new SimpleStringProperty(this, "name");

    /**
     * The observable-value that represents the graphic of this dockNode
     */
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    /**
     * The observable-value that represents that this dockNode is visible or not (showed)
     */
    private final BooleanProperty showing = new SimpleBooleanProperty(this, "showing");


    public DockNode(String title) {
        this.borderButton = new BorderButton(title);
        this.menu = new DockNodeMenu(this);
        this.dockTitleBar = new DockTitleBar(this);

        this.borderButton.graphicProperty().bind(this.graphic);
        this.borderButton.textProperty().bind(this.title);
        this.borderButton.selectedProperty().bind(this.showing);
        this.borderButton.setContextMenu(this.menu);
        this.borderButton.setOnAction(event -> setShowing(!isShowing()));

        this.title.set(title);

        this.setTop(this.dockTitleBar);
        this.getStyleClass().add(STYLE_CLASS_NAME);

        this.addListeners();

        SplitPane.setResizableWithParent(this, false);
    }

    public DockNode(Node graphic, String title) {
        this(title);
        this.setGraphic(graphic);
    }

    public DockNode(Node graphic, String title, Node content) {
        this(graphic, title);
        this.setCenter(content);
    }

    private void addListeners() {
        this.showing.addListener((observable, wasShowing, isShowing) -> {
            System.out.println("Step 1");
            if (getDockSystem() == null) return;
            System.out.println("Step 2");
            var viewMode = this.viewMode.get();
            if (isShowing) {
                viewMode.show(this);
            } else {
                viewMode.hide(this);
            }
        });

        this.dockPosition.addListener((observable, oldValue, newValue) -> {
            if (getDockSystem() == null) return;

            if (getViewMode() == ViewMode.PINNED) {
                getDockSystem().dock(newValue, this);
            }
        });

        this.viewMode.addListener((observable, oldValue, newValue) -> {
            if (getDockSystem() == null) return;

            Objects.requireNonNull(newValue, "The viewMode mustn't be null");

            oldValue.hide(this);
            newValue.show(this);

        });
    }

    public ViewMode getViewMode() {
        return viewMode.get();
    }

    public SimpleObjectProperty<ViewMode> viewModeProperty() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode.set(viewMode);
    }

    public void setShowing(boolean value) {
        this.showing.set(value);
    }



    public DockNodeMenu getMenu() {
        return menu;
    }

    public void setContent(Node value) {
        this.setCenter(value);
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

    public DockTitleBar getDockTitleBar() {
        return dockTitleBar;
    }

    public DockSystem<?> getDockSystem() {
        return dockSystem.get();
    }

    public DockPosition getDockPosition() {
        return dockPosition.get();
    }

    public void setDockPosition(DockPosition dockPosition) {
        this.dockPosition.set(dockPosition);
    }

    public void setDockSystem(DockSystem<?> dockSystem) {
        this.dockSystem.set(dockSystem);
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



}
