package com.dansoftware.libraryapp.gui.browser;

import com.dansoftware.libraryapp.util.DocumentOpener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Objects;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWord;

/**
 * A BrowserToolBar is a GUI element for operating on a particular {@link WebContentRenderer}.
 * <p>
 * A BrowserToolBar can do operations on a WebContentRenderer such as:
 * <ul>
 *     <li>reloading the page</li>
 *     <li>copying the URL of the page</li>
 *     <li>Going forward and backward through the pages</li>
 *     <li>Opening the URL in the default browsers</li>
 *     <li>Showing the actual URL in a textfield</li>
 * </ul>
 *
 * <i>BrowserToolBars are mainly used by Browsers.</i>
 *
 * @see Browser
 */
public class BrowserToolBar extends BorderPane {

    private final WebContentRenderer webContentRenderer;

    /**
     * Creates a normal BrowserToolBar that operates on the given webContentRenderer.
     *
     * @param webContentRenderer the webView that the toolbar should operate on
     * @throws NullPointerException if the webView is null
     */
    public BrowserToolBar(WebContentRenderer webContentRenderer) {
        this.webContentRenderer = Objects.requireNonNull(webContentRenderer, "The webView shouldn't be null");

        this.setCenter(createUrlIndicatorField());
        this.setLeft(new HBox(2, createPreviousPageBtn(), createForwardPageBtn(), createReloadButton()));
        this.setRight(new HBox(5, createURLCopyButton(), createDefaultBrowserOpenerButton()));

        this.getStyleClass().add("browser-toolbar");
    }

    private Button createPreviousPageBtn() {
        Button previousBtn = new Button();
        previousBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        previousBtn.setOnAction(event -> webContentRenderer.goToPreviousPage());

        Tooltip tooltip = new Tooltip(getGeneralWord("browser.toolbar.backward"));
        previousBtn.setTooltip(tooltip);

        previousBtn.getStyleClass().add("browser-previous-btn");

        return previousBtn;
    }

    private Button createForwardPageBtn() {
        Button forwardBtn = new Button();
        forwardBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        forwardBtn.setOnAction(event -> this.webContentRenderer.goToForwardPage());

        Tooltip tooltip = new Tooltip(getGeneralWord("browser.toolbar.forward"));
        forwardBtn.setTooltip(tooltip);

        forwardBtn.getStyleClass().add("browser-forward-btn");

        return forwardBtn;
    }

    private Node createUrlIndicatorField() {

        Label urlIndicator = new Label();

        urlIndicator.setText("LibraryApp Browser");
        webContentRenderer.onLocationChanged(urlIndicator::setText);
        urlIndicator.setDisable(true);

        urlIndicator.getStyleClass().add("browser-url-indicator-field");

        return urlIndicator;
    }

    private Button createURLCopyButton() {
        Button copier = new Button();
        copier.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        copier.setOnAction(event -> {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(this.webContentRenderer.getLocation());

            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        });

        Tooltip tooltip = new Tooltip(getGeneralWord("browser.toolbar.copy.url"));
        copier.setTooltip(tooltip);

        copier.getStyleClass().add("browser-url-copier-btn");

        return copier;
    }

    private Button createDefaultBrowserOpenerButton() {
        Button opener = new Button();
        opener.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        opener.setOnAction(event -> DocumentOpener.getOpener().browse(this.webContentRenderer.getLocation()));

        Tooltip tooltip = new Tooltip(getGeneralWord("browser.toolbar.open.browser"));
        opener.setTooltip(tooltip);

        opener.getStyleClass().add("browser-default-opener-btn");

        return opener;
    }

    private Button createReloadButton() {
        Button reloadButton = new Button();
        reloadButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Tooltip tooltip = new Tooltip(getGeneralWord("browser.toolbar.reload"));
        reloadButton.setTooltip(tooltip);

        reloadButton.setOnAction(event -> this.webContentRenderer.reload());

        reloadButton.getStyleClass().add("browser-reload-btn");

        return reloadButton;
    }

}
