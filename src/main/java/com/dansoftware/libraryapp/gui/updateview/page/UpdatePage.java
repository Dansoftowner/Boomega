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
import java.util.Objects;
import java.util.function.Supplier;

public abstract class UpdatePage extends StackPane implements Initializable {

    private final UpdateInformation information;
    private final UpdateView updateView;

    private final UpdatePage previousPage;
    private UpdatePage nextPage;
    private Supplier<UpdatePage> nextPageFactory;

    public UpdatePage(@NotNull UpdateView updateView, @NotNull UpdateInformation information, URL fxmlResource) {
        this(updateView, null, information, fxmlResource);
    }

    public UpdatePage(@NotNull UpdateView updateView,
                      @Nullable UpdatePage previousPage,
                      @NotNull UpdateInformation information,
                      @NotNull URL fxmlResource) {
        this(updateView, previousPage, null, information, fxmlResource);
    }

    public UpdatePage(@NotNull UpdateView updateView,
                      @Nullable UpdatePage previousPage,
                      @Nullable Supplier<@NotNull UpdatePage> nextPageFactory,
                      @NotNull UpdateInformation information,
                      @NotNull URL fxmlResource) {
        this.information = information;
        this.updateView = updateView;
        this.previousPage = previousPage;
        this.nextPageFactory = nextPageFactory;

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(I18N.getFXMLValues());

        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void goNext() {
        if (this.nextPageFactory != null) {
            this.nextPage = Objects.isNull(nextPage) ? nextPageFactory.get() : nextPage;

            getUpdateView().setUpdatePage(nextPage);
        }
    }

    protected UpdateView getUpdateView() {
        return updateView;
    }

    protected UpdateInformation getInformation() {
        return this.information;
    }

    protected void setNextPageFactory(@Nullable Supplier<@NotNull UpdatePage> nextPageFactory) {
        this.nextPageFactory = nextPageFactory;

    }

    public void reload() {
        //empty
    }

    public void onFocus(@NotNull UpdateView updateView) {
        updateView.getPrevBtn().setDisable(previousPage == null);
        updateView.getPrevBtn().setOnClick(event -> {
            if (previousPage != null)
                updateView.setUpdatePage(previousPage);
        });
        updateView.setPrefWidth(USE_COMPUTED_SIZE);
        updateView.setPrefHeight(USE_COMPUTED_SIZE);
    }
}
