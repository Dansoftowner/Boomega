package com.dansoftware.libraryapp.gui.browser;

import com.dansoftware.libraryapp.util.DocumentOpener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import java.util.Objects;

import static com.dansoftware.libraryapp.util.Bundles.getCommonBundle;

/**
 * A BrowserToolBar is a GUI element for operating on a particular {@link WebView}.
 * <p>
 * p>
 *  BrowserToolBar can do operations on a WebView such as:
 * <ul>
 *     <li>reloading the page</li>
 *     <li>copying the URL of the page</li>
 *     <li>Going forward and backward through the pages</li>
 *     <li>Opening the URL in the default browsers</li>
 *     <li>Showing the actual URL in a textfield</li>
 * </ul>
 */
public class BrowserToolBar extends BorderPane {

    private final BrowserComponent browserComponent;

    /**
     * Creates a normal BrowserToolBar
     *
     * @param browserComponent the webView that the toolbar should operate on
     * @throws NullPointerException if the webView is null
     */
    public BrowserToolBar(BrowserComponent browserComponent) {
        this.browserComponent = Objects.requireNonNull(browserComponent, "The webView shouldn't be null"::toString);

        this.setCenter(createUrlIndicatorField());
        this.setLeft(new HBox(2, createPreviousPageBtn(), createForwardPageBtn(), createReloadButton()));
        this.setRight(new HBox(5, createURLCopyButton(), createDefaultBrowserOpenerButton()));

        this.getStyleClass().add("browser-toolbar");
    }

    private Button createPreviousPageBtn() {
        Button previousBtn = new Button();
        previousBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        previousBtn.setOnAction(event -> browserComponent.goToPreviousPage());

        Tooltip tooltip = new Tooltip(getCommonBundle().getString("browser.backward"));
        previousBtn.setTooltip(tooltip);

        previousBtn.getStyleClass().add("browser-previous-btn");

        return previousBtn;
    }

    private Button createForwardPageBtn() {
        Button forwardBtn = new Button();
        forwardBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        forwardBtn.setOnAction(event -> this.browserComponent.goToForwardPage());

        Tooltip tooltip = new Tooltip(getCommonBundle().getString("browser.forward"));
        forwardBtn.setTooltip(tooltip);

        forwardBtn.getStyleClass().add("browser-forward-btn");

        return forwardBtn;
    }

    private Node createUrlIndicatorField() {

        Label urlIndicator = new Label();

        urlIndicator.setText("LibraryApp Browser");
        browserComponent.locationProperty().ifPresent(urlIndicator.textProperty()::bind);
        urlIndicator.setDisable(true);

        urlIndicator.getStyleClass().add("browser-url-indicator-field");

        return urlIndicator;
    }

    private Button createURLCopyButton() {
        Button copier = new Button();
        copier.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        copier.setOnAction(event -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(this.browserComponent.getLocation());

            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        });

        Tooltip tooltip = new Tooltip(getCommonBundle().getString("browser.copyurl"));
        copier.setTooltip(tooltip);

        copier.getStyleClass().add("browser-url-copier-btn");

        return copier;
    }

    private Button createDefaultBrowserOpenerButton() {
        Button opener = new Button();
        opener.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        opener.setOnAction(event -> DocumentOpener.getOpener().browse(this.browserComponent.getLocation()));

        Tooltip tooltip = new Tooltip(getCommonBundle().getString("browser.openSystemBrowser"));
        opener.setTooltip(tooltip);

        opener.getStyleClass().add("browser-default-opener-btn");

        return opener;
    }

    private Button createReloadButton() {
        Button reloadButton = new Button();
        reloadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Tooltip tooltip = new Tooltip(getCommonBundle().getString("browser.reloadpage"));
        reloadButton.setTooltip(tooltip);

        reloadButton.setOnAction(event -> this.browserComponent.reload());

        reloadButton.getStyleClass().add("browser-reload-btn");

        return reloadButton;
    }

}
