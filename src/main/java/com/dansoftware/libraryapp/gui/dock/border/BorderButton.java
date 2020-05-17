package com.dansoftware.libraryapp.gui.dock.border;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BorderButton extends HBox {

    private final Label label;
    private final ObjectProperty<Node> graphicProperty = new SimpleObjectProperty<>(this, "graphic");

    public BorderButton() {
        this.label = new Label();

        this.getStyleClass().add("border-button");
    }

    public ObjectProperty<Node> graphicProperty() {
        return this.graphicProperty;
    }

    public Node getGraphic() {
        return graphicProperty.get();
    }

    public void setGraphic(Node graphic) {
        this.graphicProperty.set(graphic);
    }
}
