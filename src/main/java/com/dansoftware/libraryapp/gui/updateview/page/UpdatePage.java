package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class UpdatePage extends Region implements Initializable {
    private final UpdateInformation information;

    public UpdatePage(@NotNull UpdateInformation information, FXMLLoader fxmlLoader) {
        this.information = information;
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected UpdateInformation getInformation() {
        return this.information;
    }
}
