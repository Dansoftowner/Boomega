package com.dansoftware.libraryapp.gui.tool.documentviewer;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

/**
 * A DocumentViewerPlaceHolderGUI is a placeholder- gui panel that should
 * appear when a {@link DocumentViewer} doesn't show any document
 */
public class DocumentViewerPlaceHolderGUI extends StackPane {

    DocumentViewerPlaceHolderGUI() {
        this.getStyleClass().add("document-viewer-place-holder");

        ImageView imgView = new ImageView();
        imgView.getStyleClass().add("center-image-view");

        Label label = new Label(getGeneralWord("document.viewer.placeholder.empty"));

        Group group = new Group(new VBox(30, imgView, new StackPane(label)));

        this.getChildren().add(group);
    }
}
