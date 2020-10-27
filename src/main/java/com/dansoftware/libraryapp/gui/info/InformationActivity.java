package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.gui.context.Context;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

/**
 * An InformationActivity is used for showing the information of the application.
 *
 * @author Daniel Gyorffy
 */
public class InformationActivity {

    private final Context context;

    public InformationActivity(@NotNull Context context) {
        this.context = context;
    }

    public void show() {
        var view = new InformationView(context);
        context.showOverlay(view, false);
        StackPane.setAlignment(view, Pos.CENTER);
    }
}
