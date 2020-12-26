package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQuery;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.googlebooks.Volumes;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.ExploitativeExecutor;
import javafx.concurrent.Task;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class GoogleBooksImportPanel extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksImportPanel.class);

    private final Database database;

    private final Context context;
    private final GoogleBooksImportForm form;
    private final GoogleBooksResultPagination table;

    GoogleBooksImportPanel(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.form = createForm(context);
        this.table = buildPagination();
        this.buildUI();
    }

    private GoogleBooksImportForm createForm(Context context) {
        return new GoogleBooksImportForm(context, buildOnSearchAction());
    }

    private Consumer<GoogleBooksImportForm.SearchData> buildOnSearchAction() {
        return searchData -> {
            if (searchData.isValid())
                ExploitativeExecutor.INSTANCE.submit(buildSearchTask(searchData.asBluePrint(), 0, true));
            else ;
            //TODO: DIALOG ABOUT NOT VALID FORM
        };
    }

    private SearchTask buildSearchTask(GoogleBooksImportForm.SearchData.BluePrint searchData,
                                       int startIndex,
                                       boolean starterTask) {
        var searchTask = new SearchTask(searchData, startIndex);
        searchTask.setOnRunning(e -> context.showIndeterminateProgress());
        searchTask.setOnFailed(e -> {
            context.stopProgress();
            Throwable exception = e.getSource().getException();
            logger.error("Search problem ", exception);
            context.showErrorDialog(
                    I18N.getGoogleBooksImportValue("google.books.search.failed.title"),
                    I18N.getGoogleBooksImportValue("google.books.search.failed.msg"),
                    (Exception) exception,
                    buttonType -> {
                    });
        });
        searchTask.setOnSucceeded(e -> {
            context.stopProgress();
            form.setExpanded(false);
            Volumes volumes = searchTask.getValue();
            logger.debug("Total items: {}", volumes.getTotalItems());
            if (starterTask) {
                table.setItemsPerPage(searchData.getMaxResults());
                table.setTotalItems(volumes.getTotalItems());
            }
            List<Volume> items = volumes.getItems();
            if (items != null && !items.isEmpty()) {
                table.setOnContentRequest((start, size) -> ExploitativeExecutor.INSTANCE.submit(buildSearchTask(searchData, start, false)));
                table.setItems(items.stream().map(Volume::getVolumeInfo).collect(Collectors.toList()));
            } else
                table.clear();
        });
        return searchTask;
    }

    private GoogleBooksResultPagination buildPagination() {
        var pagination = new GoogleBooksResultPagination();
        VBox.setVgrow(pagination, Priority.ALWAYS);
        return pagination;
    }

    private void buildUI() {
        getChildren().add(form);
        getChildren().add(table);
    }

    private static final class SearchTask extends Task<Volumes> {

        private final GoogleBooksImportForm.SearchData.BluePrint searchData;
        private final int startIndex;

        SearchTask(@NotNull GoogleBooksImportForm.SearchData.BluePrint searchData, int startIndex) {
            this.searchData = searchData;
            this.startIndex = startIndex;
        }

        @Override
        protected Volumes call() throws Exception {
            GoogleBooksQuery query = new GoogleBooksQueryBuilder()
                    .inText(searchData.getGeneralText())
                    .inAuthor(searchData.getAuthor())
                    .inTitle(searchData.getTitle())
                    .inPublisher(searchData.getPublisher())
                    .isbn(searchData.getIsbn())
                    .language(searchData.getLanguage())
                    .printType(searchData.getFilter().getType())
                    .sortType(searchData.getSort().getType())
                    .maxResults(searchData.getMaxResults())
                    .startIndex(startIndex)
                    .build();
            return query.load();
        }
    }
}

