package com.dansoftware.libraryapp.gui.firsttime.dialog.segment;

import com.dansoftware.sgmdialog.TitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public abstract class FixedSegment extends TitledSegment {

    private Node centerContent;

    public FixedSegment(@NotNull String name, @NotNull String title) {
        super(name, title);
    }

    protected abstract Node loadCenterContent();

    @Override
    protected final Node getCenterContent() {
        if (centerContent == null)
            centerContent = loadCenterContent();
        return centerContent;
    }
}
