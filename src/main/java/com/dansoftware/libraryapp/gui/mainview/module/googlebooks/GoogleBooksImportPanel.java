package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQuery;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.googlebooks.Volumes;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.ExploitativeExecutor;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

class GoogleBooksImportPanel extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksImportPanel.class);

    private final Database database;

    private final Context context;
    private final GoogleBooksImportForm form;
    private final GoogleBooksSearchResultTable table;

    GoogleBooksImportPanel(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.form = new GoogleBooksImportForm(I18N.getGoogleBooksImportValues());
        this.table = buildTable();
        this.buildUI();
    }

    private GoogleBooksSearchResultTable buildTable() {
        var table = new GoogleBooksSearchResultTable(context, 0);
        VBox.setVgrow(table, Priority.ALWAYS);
        return table;
    }

    private void buildUI() {
        getChildren().add(form.getRenderer());
        getChildren().add(buildSearchButton());
        getChildren().add(table);
    }

    private Button buildSearchButton() {
        Button button = new Button(I18N.getGoogleBooksImportValue("google.books.add.form.search"));
        button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SEARCH));
        VBox.setMargin(button, new Insets(0, 20, 10, 20));
        button.setDefaultButton(true);
        button.prefWidthProperty().bind(this.widthProperty());
        button.setOnAction(e -> {
            if (form.isValid())
                ExploitativeExecutor.INSTANCE.submit(new SearchTask(context, form, table));
            else ;
                //TODO: DIALOG ABOUT NOT VALID FORM
        });
        return button;
    }

    private static final class SearchTask extends Task<Volumes> {

        private final Context context;
        private final GoogleBooksImportForm form;
        private final GoogleBooksSearchResultTable table;

        SearchTask(@NotNull Context context,
                   @NotNull GoogleBooksImportForm form,
                   @NotNull GoogleBooksSearchResultTable table) {
            this.context = context;
            this.form = form;
            this.table = table;
            this.addEventHandlers();
        }

        private void addEventHandlers() {
            setOnRunning(e -> context.showIndeterminateProgress());
            setOnFailed(e -> {
                context.stopProgress();
                Throwable exception = e.getSource().getException();
                logger.error("Search problem ", exception);
                context.showErrorDialog(
                        I18N.getGoogleBooksImportValue("google.books.search.failed.title"),
                        I18N.getGoogleBooksImportValue("google.books.search.failed.msg"),
                        (Exception) exception,
                        buttonType -> {});
            });
            setOnSucceeded(e -> {
                context.stopProgress();
                Volumes volumes = this.getValue();
                List<Volume> items = volumes.getItems();
                table.getItems().clear();
                if (items != null && !items.isEmpty())
                    table.getItems().setAll(items.stream().map(Volume::getVolumeInfo).collect(Collectors.toList()));
            });
        }

        @Override
        protected Volumes call() throws Exception {
            GoogleBooksQuery query = new GoogleBooksQueryBuilder()
                    .inText(form.getGeneralText())
                    .inAuthor(form.getAuthor())
                    .inTitle(form.getTitle())
                    .inPublisher(form.getPublisher())
                    .isbn(form.getIsbn())
                    .language(form.getLanguage())
                    .printType(form.getFilter().getType())
                    .sortType(form.getSort().getType())
                    .maxResults(form.getMaxResults())
                    .build();
            return query.load();
        }
    }
}


