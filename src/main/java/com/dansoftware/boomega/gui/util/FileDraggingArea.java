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

package com.dansoftware.boomega.gui.util;

import javafx.css.PseudoClass;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class FileDraggingArea extends Rectangle {

    private static final String STYLE_CLASS = "draggingArea";

    private final boolean multipleFiles;
    private final List<String> supportedExtensions;

    public FileDraggingArea(@NotNull List<String> supportedExtensions, boolean multipleFiles) {
        this.supportedExtensions = supportedExtensions;
        this.multipleFiles = multipleFiles;
        getStyleClass().add(STYLE_CLASS);
    }

    public FileDraggingArea(@NotNull List<String> supportedExtensions,
                            boolean multipleFiles,
                            double width,
                            double height) {
        this(supportedExtensions, multipleFiles);
        setWidth(width);
        setHeight(height);
        setFill(Color.TRANSPARENT);
    }

    public void setOnFileAction(Consumer<@Nullable List<File>> onResult) {
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(supportedExtensions.stream()
                        .map((String extension) -> {
                            var capitalCasedExt = Character.toUpperCase(extension.charAt(0)) + extension.substring(1);
                            return new FileChooser.ExtensionFilter(capitalCasedExt + " files", "*." + extension);
                        })
                        .collect(Collectors.toList()));
                if (multipleFiles) {
                    onResult.accept(fileChooser.showOpenMultipleDialog(getScene().getWindow()));
                } else {
                    File opened = fileChooser.showOpenDialog(getScene().getWindow());
                    onResult.accept(opened == null ? Collections.emptyList() : Collections.singletonList(opened));
                }
            }
        });

        setOnDragExited(e -> {
            pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
        });
        setOnDragOver(e -> {
            pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
            e.acceptTransferModes(TransferMode.COPY);
        });
        setOnDragDropped(e -> {
            if (e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles().stream()
                        .filter(file -> supportedExtensions.contains(FilenameUtils.getExtension(file.getName())))
                        .collect(Collectors.toList());
                onResult.accept(files.isEmpty() ? null : files);
            }
        });
    }
}
