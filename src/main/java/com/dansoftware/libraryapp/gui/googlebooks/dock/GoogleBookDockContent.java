package com.dansoftware.libraryapp.gui.googlebooks.dock;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.db.data.ServiceConnection;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
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
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoogleBookDockContent extends VBox {

    private final Context context;
    private final Database database;

    private List<Record> items;

    public GoogleBookDockContent(@NotNull Context context,
                                 @NotNull Database database,
                                 @Nullable List<Record> items) {
        this.context = context;
        this.database = database;
        this.setItems(items);
    }

    public void setItems(List<Record> items) {
        this.items = items == null ? Collections.emptyList() : items;
        buildBaseContent();
    }

    private void setContent(@NotNull Node content) {
        if (this.getChildren().isEmpty()) this.getChildren().add(content);
        else this.getChildren().set(0, content);
    }

    private void showProgress() {
        var progressBar = new ProgressBar();
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        getChildren().add(0, progressBar);
    }

    private void stopProgress() {
        getChildren().removeIf(e -> e instanceof ProgressBar);
    }

    private void buildBaseContent() {
        String googleHandle = retrieveGoogleBookHandle(items);
        if (googleHandle != null) {
            loadGoogleBookContent(googleHandle, e -> setContent(new ErrorPlaceHolder(context, e)));
        } else if (items.size() == 1) {
            setContent(new NoConnectionPlaceHolder(context, database, items.get(0)));
        } else if (items.size() > 1) {
            setContent(new MultipleSelectionPlaceHolder());
        } else {
            setContent(new NoSelectionPlaceHolder());
        }
    }

    @Nullable
    private String retrieveGoogleBookHandle(@NotNull List<Record> items) {
        String googleBookHandle = null;

        List<String> distinctHandles = items.stream()
                .map(record -> record.getServiceConnection().getGoogleBookLink())
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
        task.setOnRunning(e -> showProgress());
        task.setOnSucceeded(event -> {
            stopProgress();
            onSucceeded.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            stopProgress();
            onFailed.accept(event.getSource().getException());
        });
        return task;
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

    private static final class NoConnectionPlaceHolder extends StackPane {

        private final Context context;
        private final Database database;
        private final Record record;

        NoConnectionPlaceHolder(@NotNull Context context,
                                @NotNull Database database,
                                @NotNull Record record) {
            this.context = context;
            this.database = database;
            this.record = record;
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
            button.setOnAction(event -> showGoogleBookJoiner());
            return button;
        }

        public void showGoogleBookJoiner() {
            context.showOverlay(
                    new GoogleBookJoinerOverlay(context, buildSearchParameters(record),
                    volume -> ExploitativeExecutor.INSTANCE.submit(buildJoinActionTask(volume)))
            );
        }

        private SearchParameters buildSearchParameters(@NotNull Record record) {
            return new SearchParameters()
                    .printType(record.getRecordType() == Record.Type.BOOK ?
                            GoogleBooksQueryBuilder.PrintType.BOOKS :
                            GoogleBooksQueryBuilder.PrintType.MAGAZINES
                    )
                    .isbn(record.getIsbn())
                    .authors(String.join(",", Optional.ofNullable(record.getAuthors()).orElse(Collections.emptyList())))
                    .publisher(record.getPublisher())
                    .title(record.getTitle())
                    .language(record.getLanguage());
        }

        private Task<Void> buildJoinActionTask(Volume volume) {
            var task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    record.setServiceConnection(new ServiceConnection(volume.getSelfLink()));
                    database.updateRecord(record);
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
