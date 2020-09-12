package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.gui.entry.Context;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class InformationActivity {

    private final Context context;

    public InformationActivity(@NotNull Context context) {
        this.context = context;
    }

    public void show() {
        var view = new InformationView();
        context.showOverlay(view, false);
        StackPane.setAlignment(view, Pos.CENTER);
    }
}
