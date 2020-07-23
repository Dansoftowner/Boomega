package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.beans.binding.Bindings;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class UpdateActivity {

    private final UpdateInformation info;

    public UpdateActivity(@NotNull UpdateInformation info) {
        this.info = info;
    }

    public void show() {
        UpdateWindow window = new UpdateWindow(new UpdateView(info));
        window.show();
    }

}
