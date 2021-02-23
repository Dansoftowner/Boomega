package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.i18n.I18N;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

public class GoogleBookConnectionDock extends DockView<GoogleBookConnectionView> {

    public GoogleBookConnectionDock(@NotNull SplitPane parent, @NotNull GoogleBookConnectionView content) {
        super(
                parent,
                new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png")),
                I18N.getValue("google.books.dock.title"),
                content
        );
    }
}
