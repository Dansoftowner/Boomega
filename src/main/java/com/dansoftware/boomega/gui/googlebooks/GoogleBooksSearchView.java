/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.googlebooks;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.googlebooks.details.GoogleBookDetailsOverlay;
import com.dansoftware.boomega.util.concurrent.CachedExecutor;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * A {@link GoogleBooksSearchView} allows to search on Google Books (through a {@link GoogleBooksSearchForm}) and also
 * displays them (through a {@link GoogleBooksPagination}).
 *
 * @author Daniel Gyoerffy
 */
public class GoogleBooksSearchView extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksSearchView.class);

    private final Context context;
    private final GoogleBooksSearchForm form;
    private final GoogleBooksPagination tablePagination;

    private Runnable onRefreshRequest;

    GoogleBooksSearchView(@NotNull Context context) {
        this.context = context;
        this.form = createForm(context);
        this.tablePagination = buildPagination();
        this.buildUI();
        this.buildTableSelectionPolicy();
    }

    public GoogleBooksTable getTable() {
        return tablePagination.getTable();
    }

    public void refresh() {
        if (onRefreshRequest != null)
            onRefreshRequest.run();
    }

    public void clear() {
        this.tablePagination.clear();
        this.form.clear();
        this.form.setExpanded(true);
    }

    public void scrollToTop() {
        this.tablePagination.scrollToTop();
    }

    private void buildTableSelectionPolicy() {
        getTable().setOnItemDoubleClicked(item -> context.showOverlay(new GoogleBookDetailsOverlay(context, item)));
    }

    private GoogleBooksSearchForm createForm(Context context) {
        return new GoogleBooksSearchForm(context, buildOnSearchAction());
    }

    private Consumer<SearchParameters> buildOnSearchAction() {
        return searchProperties -> {
            Runnable action = () ->
                    CachedExecutor.INSTANCE.submit(buildSearchTask(searchProperties));
            action.run();
            onRefreshRequest = action;
        };
    }

    private Runnable buildSearchTask(SearchParameters searchParameters) {
        var searchTask = new GoogleBooksPaginationSearchTask(context, tablePagination, true, searchParameters);
        searchTask.setOnBeforeResultsDisplayed(() -> form.setExpanded(false));
        searchTask.setOnNewContentRequestCreated(action -> onRefreshRequest = action);
        return searchTask;
    }


    private GoogleBooksPagination buildPagination() {
        var pagination = new GoogleBooksPagination();
        VBox.setVgrow(pagination, Priority.ALWAYS);
        return pagination;
    }

    private void buildUI() {
        getChildren().add(form);
        getChildren().add(tablePagination);
    }
}


