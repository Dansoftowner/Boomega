package com.dansoftware.libraryapp.gui.util.node;

import javafx.scene.control.TextArea;

public class UnEditableTextArea extends TextArea {
    public UnEditableTextArea(String text) {
        super(text);
        this.setEditable(false);
    }
}
