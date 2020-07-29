package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

public abstract class UpdatePage extends StackPane implements Initializable {

    private final UpdateInformation information;
    private final UpdateView updateView;
    private final UpdatePage previous;

    public UpdatePage(@NotNull UpdateView updateView, @NotNull UpdateInformation information, URL fxmlResource) {
        this(updateView, null, information, fxmlResource);
    }

    public UpdatePage(@NotNull UpdateView updateView, @Nullable UpdatePage previous, @NotNull UpdateInformation information, URL fxmlResource) {
        this.information = information;
        this.updateView = updateView;
        this.previous = previous;

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
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

    public void reload() {
        //empty
    }

    public void onFocus(@NotNull UpdateView updateView) {
        updateView.getPrevBtn().setDisable(previous == null);
        updateView.getPrevBtn().setOnClick(event -> {
            if (previous != null)
                updateView.setUpdatePage(previous);
        });
        updateView.setPrefWidth(USE_COMPUTED_SIZE);
        updateView.setPrefHeight(USE_COMPUTED_SIZE);
    }
}
