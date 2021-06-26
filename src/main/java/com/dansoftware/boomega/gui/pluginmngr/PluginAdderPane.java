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

package com.dansoftware.boomega.gui.pluginmngr;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.util.BaseFXUtils;
import com.dansoftware.boomega.gui.util.FileDraggingArea;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.main.ApplicationRestart;
import com.dansoftware.boomega.plugin.PluginDirectory;
import com.restart4j.RestartException;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Gui panel for adding plugins to the application from the file system.
 * Supports drag and drop also.
 *
 * @author Daniel Gyoerffy
 */
public final class PluginAdderPane extends StackPane {

    private static final Logger logger = LoggerFactory.getLogger(PluginAdderPane.class);

    private static final String STYLE_CLASS = "pluginAdderPane";
    private static final String LABEL_STYLE_CLASS = "draggingLabel";
    private static final String WARNING_LABEL_STYLE_CLASS = "restartWarningLabel";

    private boolean restartNeeded;
    private Node restartWarningLabel;

    private final Context context;
    private final ObservableList<File> pluginList;

    public PluginAdderPane(@NotNull Context context, @NotNull ObservableList<File> pluginList) {
        this.context = context;
        this.pluginList = pluginList;
        getStyleClass().add(STYLE_CLASS);
        getChildren().add(buildDraggingArea());
        Label label = new Label(I18N.getValue("plugin.module.adder.dragging.comment"));
        label.setDisable(true);
        label.getStyleClass().add(LABEL_STYLE_CLASS);
        getChildren().add(label);
    }

    private Node buildDraggingArea() {
        FileDraggingArea draggingArea = new FileDraggingArea(Collections.singletonList("jar"), false, 600, 400);
        draggingArea.setOnFileAction(files -> {
            if (files != null && !files.isEmpty()) {
                registerPlugin(files);
            }
        });
        return draggingArea;
    }

    private void registerPlugin(@NotNull List<File> files) {
        logger.debug("Registration request {}", files);
        context.showConfirmationDialog(
                I18N.getValue("plugin.add.warning.title"),
                I18N.getValue("plugin.add.warning.msg"),
                buttonType -> {
                    if (BaseFXUtils.typeEquals(buttonType, ButtonType.YES)) {

                        for (File file : files) {
                            PluginDirectory pluginDirectory = PluginDirectory.INSTANCE;
                            try {
                                pluginDirectory.addPlugin(file);
                                pluginList.add(file);
                                restartNeeded = true;
                                context.showInformationDialog(
                                        I18N.getValue("plugin.add.success.title"),
                                        I18N.getValue("plugin.add.success.msg"), r -> {
                                        });
                            } catch (IOException e) {
                                logger.error("Couldn't add plugin", e);
                                context.showErrorDialog(
                                        I18N.getValue("plugin.add.failed.title", file.getName()),
                                        I18N.getValue("plugin.add.failed.msg"), e, r -> {
                                        });
                            }
                        }

                        if (restartNeeded && restartWarningLabel == null) {
                            logger.debug("Restart needed and building warning label");
                            restartWarningLabel = buildRestartWarningLabel();
                            getChildren().add(restartWarningLabel);
                        }
                    }
                }
        );
    }

    private Node buildRestartWarningLabel() {
        var restartLabel = new Hyperlink(I18N.getValue("plugin.add.restart.label"));
        restartLabel.getStyleClass().add(WARNING_LABEL_STYLE_CLASS);
        StackPane.setAlignment(restartLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(restartLabel, new Insets(0, 0, 10, 0));
        restartLabel.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                try {
                    new ApplicationRestart().restartApp();
                } catch (RestartException restartException) {
                    logger.error("Couldn't restart the application", restartException);
                    context.showErrorDialog("Error", "Error", restartException);
                }
            }
        });
        return restartLabel;
    }

}
