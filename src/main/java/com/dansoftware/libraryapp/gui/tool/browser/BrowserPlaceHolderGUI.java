package com.dansoftware.libraryapp.gui.tool.browser;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

/**
 * A BrowserPlaceHolderGUI is a placeholder- gui panel that should
 * appear when a {@link Browser} doesn't show any website
 */
public class BrowserPlaceHolderGUI extends StackPane {

    BrowserPlaceHolderGUI() {
        this.getStyleClass().add("browser-place-holder");

        ImageView imgView = new ImageView();
        imgView.getStyleClass().add("center-image-view");

        Label label = new Label(getGeneralWord("browser.placeholder.empty"));

        Group group = new Group(new VBox(30, imgView, new StackPane(label)));

        this.getChildren().add(group);
    }

}
