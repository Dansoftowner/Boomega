package com.dansoftware.libraryapp.gui.util;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import org.apache.commons.lang3.StringUtils;

/**
 * A SpaceValidator can be used for {@link TextInputControl} objects (for example: {@link javafx.scene.control.TextField})
 * to avoid whitespaces.
 */
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
