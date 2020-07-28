package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class UpdatePage extends StackPane implements Initializable {
    private final UpdateInformation information;
    private final UpdateView updateView;

    public UpdatePage(@NotNull UpdateView updateView, @NotNull UpdateInformation information, FXMLLoader fxmlLoader) {
        this.information = information;
        this.updateView = updateView;
        fxmlLoader.setController(this);
        fxmlLoader.setResources(I18N.getFXMLValues());

        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected UpdateView getUpdateView() {
        return updateView;
    }

    protected UpdateInformation getInformation() {
        return this.information;
    }
}
