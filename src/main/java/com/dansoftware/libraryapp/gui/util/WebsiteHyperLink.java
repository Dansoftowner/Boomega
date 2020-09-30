package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.util.SystemBrowser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link WebsiteHyperLink} is a {@link Hyperlink} implementation that can
 * open websites in the default browser easily.
 *
 * <p>
 * It also has a {@link Tooltip} that shows the url.
 *
 * @author Daniel Gyorffy
 */
public class WebsiteHyperLink
        extends Hyperlink
        implements EventHandler<ActionEvent> {

    private final StringProperty url;

    public WebsiteHyperLink(@NotNull String text,
                            @NotNull String url) {
        this.url = new SimpleStringProperty(url);
        setTooltip(new HyperlinkTooltip(this));
        setText(text);
        setOnAction(this);
    }

    @Override
    public void handle(ActionEvent event) {
        if (SystemBrowser.isSupported()) {
            SystemBrowser systemBrowser = new SystemBrowser();
            systemBrowser.browse(url.get());
        }
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    /**
     * The tooltip implementation
     */
    private static final class HyperlinkTooltip extends Tooltip {
        HyperlinkTooltip(WebsiteHyperLink parent) {
            textProperty().bind(parent.url);
        }
    }
}
