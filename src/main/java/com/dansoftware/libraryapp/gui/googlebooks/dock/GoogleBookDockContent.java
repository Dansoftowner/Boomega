package com.dansoftware.libraryapp.gui.googlebooks.dock;

import com.dansoftware.libraryapp.googlebooks.SingleGoogleBookQuery;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBookDetailsPane;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.gui.googlebooks.join.GoogleBookJoinerOverlay;
import com.dansoftware.libraryapp.i18n.I18N;
import com.dansoftware.libraryapp.util.ExploitativeExecutor;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoogleBookDockContent<T> extends VBox {

    private final Context context;
    private final BiConsumer<T, Volume> joinAction;
    private final BiConsumer<T, Volume> removeAction;
    private final Function<T, SearchParameters> searchParametersSupplier;
    private final Function<T, String> volumeHandleRetriever;

    private List<T> items;

    public GoogleBookDockContent(@NotNull Context context,
                                 @Nullable List<T> items,
                                 @NotNull BiConsumer<T, Volume> joinAction,
                                 @NotNull BiConsumer<T, Volume> removeAction,
                                 @NotNull Function<T, SearchParameters> searchParametersSupplier,
                                 @NotNull Function<T, String> volumeHandleRetriever) {
        this.context = context;
        this.joinAction = joinAction;
        this.removeAction = removeAction;
        this.searchParametersSupplier = searchParametersSupplier;
        this.volumeHandleRetriever = volumeHandleRetriever;
        this.setItems(items);
    }

    public void setItems(List<T> items) {
        this.items = items == null ? Collections.emptyList() : items;
        buildBaseContent();
    }

    private void setContent(@NotNull Node content) {
        if (this.getChildren().isEmpty()) this.getChildren().add(content);
        else this.getChildren().set(0, content);
    }

    private void buildBaseContent() {
        String googleHandle = retrieveGoogleBookHandle(items);
        if (googleHandle != null) {
            loadGoogleBookContent(googleHandle, e -> setContent(new ErrorPlaceHolder(context, e)));
        } else if (items.size() == 1) {
            setContent(new NoConnectionPlaceHolder<T>(new CompleteJoinAction<>(context, items.get(0), searchParametersSupplier, joinAction)));
        } else if (items.size() > 1) {
            setContent(new MultipleSelectionPlaceHolder());
        } else {
            setContent(new NoSelectionPlaceHolder());
        }
    }

    @Nullable
    private String retrieveGoogleBookHandle(@NotNull List<T> items) {
        String googleBookHandle = null;

        List<String> distinctHandles = items.stream()
                .map(volumeHandleRetriever)
                .distinct()
                .collect(Collectors.toList());

        if (distinctHandles.size() == 1)
            googleBookHandle = distinctHandles.get(0);
        return googleBookHandle;
    }

    private void loadGoogleBookContent(String googleBookLink, Consumer<Throwable> onFailed) {
        ExploitativeExecutor.INSTANCE.submit(
                buildVolumePullTask(
                        googleBookLink,
                        onFailed,
                        volume -> setContent(new GoogleBookDetailsPane(context, volume))
                )
        );
    }

    private Task<Volume> buildVolumePullTask(String googleBook,
                                             Consumer<Throwable> onFailed,
                                             Consumer<Volume> onSucceeded) {
        var task = new Task<Volume>() {
            @Override
            protected Volume call() throws Exception {
                return new SingleGoogleBookQuery(googleBook).load();
            }
        };
        task.setOnSucceeded(event -> onSucceeded.accept(task.getValue()));
        task.setOnFailed(event -> onFailed.accept(event.getSource().getException()));
        return task;
    }

    private static final class CompleteJoinAction<T> {

        private final Context context;
        private final T item;
        private final Function<T, SearchParameters> searchParametersSupplier;
        private final BiConsumer<T, Volume> joinAction;

        CompleteJoinAction(@NotNull Context context,
                           @NotNull T item,
                           @NotNull Function<T, SearchParameters> searchParametersSupplier,
                           @NotNull BiConsumer<T, Volume> joinAction) {
            this.context = context;
            this.item = item;
            this.searchParametersSupplier = searchParametersSupplier;
            this.joinAction = joinAction;
        }

        public void run() {
            context.showOverlay(new GoogleBookJoinerOverlay(context, searchParametersSupplier.apply(item), volume -> {
                ExploitativeExecutor.INSTANCE.submit(buildJoinActionTask(volume));
            }));
        }

        private Task<Void> buildJoinActionTask(Volume volume) {
            var task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    joinAction.accept(item, volume);
                    return null;
                }
            };
            task.setOnRunning(event -> context.showIndeterminateProgress());
            task.setOnFailed(event -> {
                //TODO: ERROR DIALOG
                context.stopProgress();
            });
            task.setOnSucceeded(event -> {
                context.stopProgress();
                //TODO: NOTIFICATION MESSAGE
            });
            return task;
        }
    }

    private static final class ErrorPlaceHolder extends StackPane {

        private final Context context;
        private final Throwable cause;

        ErrorPlaceHolder(@NotNull Context context, Throwable cause) {
            this.context = context;
            this.cause = cause;
            buildUI();
        }

        private void buildUI() {
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            this.getChildren().add(
                    new Group(new VBox(5,
                            new Label(I18N.getGoogleBooksImportValue("google.books.dock.placeholder.error")),
                            buildDetailsButton()))
            );
            VBox.setVgrow(this, Priority.ALWAYS);
        }

        private Button buildDetailsButton() {
            var button = new Button(
                    I18N.getGoogleBooksImportValue("google.books.dock.placeholder.error.details"),
                    new MaterialDesignIconView(MaterialDesignIcon.DETAILS)
            );
            button.setOnAction(event -> context.showErrorDialog(StringUtils.EMPTY, StringUtils.EMPTY, (Exception) cause));
            return button;
        }
    }

    private static final class NoConnectionPlaceHolder<T> extends StackPane {

        private final CompleteJoinAction<T> joinAction;

        NoConnectionPlaceHolder(@NotNull CompleteJoinAction<T> joinAction) {
            this.joinAction = joinAction;
            buildUI();
        }

        private void buildUI() {
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            getChildren().add(new VBox(5,
                    new Label(I18N.getGoogleBooksImportValue("google.books.dock.placeholder.noconn")),
                    buildConnectionButton())
            );
            VBox.setVgrow(this, Priority.ALWAYS);
        }

        private Button buildConnectionButton() {
            var button = new Button(
                    I18N.getGoogleBooksImportValue("google.books.dock.connection"),
                    new MaterialDesignIconView(MaterialDesignIcon.GOOGLE)
            );
            button.setOnAction(event -> joinAction.run());
            return button;
        }
    }

    private static final class MultipleSelectionPlaceHolder extends StackPane {

        MultipleSelectionPlaceHolder() {
            buildUI();
        }

        private void buildUI() {
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            this.getChildren().add(new Label(I18N.getGoogleBooksImportValue("google.books.dock.placeholder.multiple")));
            VBox.setVgrow(this, Priority.ALWAYS);
        }
    }

    private static final class NoSelectionPlaceHolder extends StackPane {

        NoSelectionPlaceHolder() {
            buildUI();
        }

        private void buildUI() {
            this.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            this.getChildren().add(new Label(I18N.getGoogleBooksImportValue("google.books.dock.placeholder.noselection")));
            VBox.setVgrow(this, Priority.ALWAYS);
        }

    }

}
