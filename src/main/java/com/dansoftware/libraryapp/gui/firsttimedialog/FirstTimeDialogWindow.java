package com.dansoftware.libraryapp.gui.firsttimedialog;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class FirstTimeDialogWindow {

    private static final class FocusWindow extends LibraryAppStage {
        FocusWindow(@NotNull Stage owner) {
            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);
            setAlwaysOnTop(true);
            setOnCloseRequest(event -> owner.close());
            setResizable(false);
        }
    }

    private final Stage owner;
    private final Stage focused;

    public FirstTimeDialogWindow() {
        owner = new Stage(StageStyle.UTILITY);
        owner.setOpacity(0.0);

        focused = new FocusWindow(owner);
    }

    public void showAndWait() {
        owner.showAndWait();
        focused.showAndWait();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return focused.showingProperty();
    }
}
