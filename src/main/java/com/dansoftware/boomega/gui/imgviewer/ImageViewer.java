/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
