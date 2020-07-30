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

/**
 * An UpdatePage is used by an {@link UpdateView} to display a particular
 * gui-view that lets the user to do/see things that is necessary in the theme of updating.
 *
 * <p>
 * The {@link UpdatePage} itself is an abstract-structure; it should be extended.
 *
 * <p>
 * The {@link UpdatePage} also loads an FXML resource inside it and it's also a <i>controller</i> for it
 * that's why it implements the {@link javafx.fxml.Initializable} interface.
 *
 * @author Daniel Gyorffy
 */
public abstract class UpdatePage extends StackPane implements Initializable {

    private final UpdateInformation information;
    private final UpdateView updateView;

    private final UpdatePage previousPage;
    private UpdatePage nextPage;
    private Supplier<UpdatePage> nextPageFactory;

    /**
     * Creates an UpdatePage that doesn't point to any previous or next update-page.
     *
     * @see #UpdatePage(UpdateView, UpdatePage, UpdateInformation, URL)
     */
    protected UpdatePage(@NotNull UpdateView updateView, @NotNull UpdateInformation information, @NotNull URL fxmlResource) {
        this(updateView, null, information, fxmlResource);
    }

    /**
     * Creates an UpdatePage with all necessary values <b>except</b> the <i>nextPageFactory</i>, so it will
     * not point to any next update-page.
     *
     * @see #UpdatePage(UpdateView, UpdatePage, Supplier, UpdateInformation, URL)
     */
    protected UpdatePage(@NotNull UpdateView updateView,
                         @Nullable UpdatePage previousPage,
                         @NotNull UpdateInformation information,
                         @NotNull URL fxmlResource) {
        this(updateView, previousPage, null, information, fxmlResource);
    }

    /**
     * Creates an UpdatePage with <b>all</b> necessary values.
     *
     * @param updateView the "parent" {@link UpdateView} that displays the updatePage
     * @param previousPage the update-page that is before this
     *                     (the "previous" toolbar-button in the {@link UpdateView}
     *                     will navigate to the previous update-page defined here)
     * @param nextPageFactory the {@link Supplier} that creates the update-page that should be appear after this update-page;
     *                        it can be set later by the {@link #setNextPageFactory(Supplier)} method.
     * @param information the {@link UpdateInformation} object that holds all information about the new update
     * @param fxmlResource the Resource-Locator that defines what fxml resource should be loaded into this update-page
     */
    protected UpdatePage(@NotNull UpdateView updateView,
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

    /**
     * Displays the next {@link UpdatePage} in the "parent" {@link UpdateView}.
     *
     * Uses the {@link #nextPageFactory} for creating the next update-page if it's not created yet.
     */
    protected void goNext() {
        if (this.nextPageFactory != null) {
            // if the reference to the next-page points to null, we create the next update-page
            this.nextPage = Objects.isNull(nextPage) ? nextPageFactory.get() : nextPage;

            getUpdateView().setUpdatePage(nextPage);
        }
    }

    /**
     * It's usually called by an {@link UpdateView} object when the UpdatePage
     * is displayed inside it.
     *
     * <p>
     *     ....
     *
     * @param updateView it should be the update-view that called this method
     */
    public void onFocus(@NotNull UpdateView updateView) {
        updateView.getPrevBtn().setDisable(previousPage == null);
        updateView.getPrevBtn().setOnClick(event -> {
            if (previousPage != null)
                updateView.setUpdatePage(previousPage);
        });
        updateView.setPrefWidth(USE_COMPUTED_SIZE);
        updateView.setPrefHeight(USE_COMPUTED_SIZE);
    }

    /**
     * "Reloads" the UpdatePage. That what it do exactly is depends
     * on the particular implementations.
     */
    public void reload() {
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


}
