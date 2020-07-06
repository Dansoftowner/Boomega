package com.dansoftware.libraryapp.gui.util;

import javafx.scene.control.TextFormatter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.UnaryOperator;

public class SpaceValidator extends TextFormatter<TextFormatter.Change> {
    public SpaceValidator() {
        super(change -> {
            String text = change.getText();
            if (StringUtils.isNotEmpty(text)) {
                change.setText(text.replaceAll("\\s+", StringUtils.EMPTY));
            }

            return change;
        });
    }
}
