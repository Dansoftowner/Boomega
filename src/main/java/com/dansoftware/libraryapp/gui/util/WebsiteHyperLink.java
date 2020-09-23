package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.util.SystemBrowser;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import org.jetbrains.annotations.NotNull;

public class WebsiteHyperLink
        extends Hyperlink
        implements EventHandler<ActionEvent> {

    private static final class HyperlinkTooltip extends Tooltip {
        HyperlinkTooltip(WebsiteHyperLink parent) {
            textProperty().bind(parent.textProperty());
        }
    }

    private final String url;

    public WebsiteHyperLink(@NotNull String text, @NotNull String url) {
        this.url = url;
        setTooltip(new HyperlinkTooltip(this));
        setText(text);
        setOnAction(this);
    }

    @Override
    public void handle(ActionEvent event) {
        SystemBrowser systemBrowser = new SystemBrowser();
        systemBrowser.browse(url);
    }
}
