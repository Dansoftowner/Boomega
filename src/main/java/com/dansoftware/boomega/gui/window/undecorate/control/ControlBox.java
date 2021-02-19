package com.dansoftware.boomega.gui.window.undecorate.control;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class ControlBox extends HBox {

    private static final String STYLE_CLASS = "windowControlBox";

    ControlBox(@NotNull Stage stage) {
        getStyleClass().add(STYLE_CLASS);
        getChildren().addAll(
                new MinimizeButton(stage),
                new RestoreButton(stage),
                new CloseButton(stage)
        );
    }

    private static final class CloseButton extends Button {
        private static final String STYLE_CLASS = "closeButton";

        CloseButton(@NotNull Stage stage) {
            setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "16.5"));
            getStyleClass().add(STYLE_CLASS);
            setOnAction(e -> stage.close());
        }
    }

    private static final class RestoreButton extends Button {
        private static final String STYLE_CLASS = "restoreButton";

        RestoreButton(@NotNull Stage stage) {
            setGraphic(new MaterialDesignIconView(MaterialDesignIcon.WINDOW_RESTORE, "15"));
            getStyleClass().add(STYLE_CLASS);
            setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        }
    }

    private static final class MinimizeButton extends Button {
        private static final String STYLE_CLASS = "minimizeButton";

        MinimizeButton(@NotNull Stage stage) {
            setGraphic(new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MINIMIZE, "15"));
            getStyleClass().add(STYLE_CLASS);
            setOnAction(e -> {
                stage.setIconified(true);
            });
        }
    }

}
