package com.dansoftware.libraryapp.gui.workbench;

import com.dlsc.workbenchfx.Workbench;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;

import java.util.Objects;
import java.util.function.Supplier;

public class LibraryAppWorkbench extends Workbench {

    private Supplier<Region> loadingOverlaySupplier;

    public void showLoadingOverlay() {
        Region overlay = new com.jfoenix.controls.JFXProgressBar(Timeline.INDEFINITE);
        this.loadingOverlaySupplier = () -> overlay;

        this.showOverlay(overlay, true);
    }

    public void closeLoadingOverlay() {
        if (Objects.isNull(this.loadingOverlaySupplier))
            return;

        this.hideOverlay(this.loadingOverlaySupplier.get());
        this.loadingOverlaySupplier = null;
    }
}

