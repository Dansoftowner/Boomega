package com.dansoftware.boomega.gui.imgviewer;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;
import org.jetbrains.annotations.NotNull;

/**
 * Used for showing an image. Provides zooming functionality.
 *
 * @author Daniel Gyorffy
 */
public class ImageViewer extends GesturePane implements ContextTransformable {

    public ImageViewer(Image image) {
        super(new ImageView(image));
        buildPolicy();
    }

    private void buildPolicy() {
        setOnMouseClicked(e -> {
            Point2D pivotOnTarget = this.targetPointAt(new Point2D(e.getX(), e.getY()))
                    .orElse(this.targetPointAtViewportCentre());
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                this.animate(Duration.millis(300))
                        .interpolateWith(Interpolator.EASE_BOTH)
                        .zoomBy(this.getCurrentScale(), pivotOnTarget);
            } else if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 1) {
                this.animate(Duration.millis(300))
                        .interpolateWith(Interpolator.EASE_BOTH)
                        .zoomTo(this.getMinScale(), pivotOnTarget);
            }
        });
    }

    @Override
    public @NotNull Context getContext() {
        return Context.empty();
    }
}
