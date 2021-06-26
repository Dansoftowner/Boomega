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

import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.service.googlebooks.Volume;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * A {@link GoogleBooksPagination} used for showing Google Books elements
 * in a {@link GoogleBooksTable}, but also provides a pagination control for loading
 * new and new content.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBooksPagination extends VBox {

    private final HeaderArea headerArea;
    private final GoogleBooksTable table;

    private final IntegerProperty totalItems;
    private final IntegerProperty itemsPerPage;

    public GoogleBooksPagination() {
        this.totalItems = new SimpleIntegerProperty();
        this.itemsPerPage = new SimpleIntegerProperty();
        this.table = buildTable();
        this.headerArea = buildHeaderArea();
        this.buildUI();
    }

    private GoogleBooksTable buildTable() {
        var table = new GoogleBooksTable(0);
        VBox.setVgrow(table, Priority.ALWAYS);
        return table;
    }

    private HeaderArea buildHeaderArea() {
        return new HeaderArea(totalItems, itemsPerPage, table.startIndexProperty());
    }

    private void buildUI() {
        this.getChildren().add(table);
    }

    public void setItems(List<Volume> items) {
        if (items != null && !items.isEmpty()) {
            table.getItems().clear();
            table.getItems().addAll(items);
            if (!this.getChildren().contains(headerArea))
                this.getChildren().add(0, headerArea);
        }
    }

    public void scrollToTop() {
        table.scrollTo(0);
    }

    public void clear() {
        table.getItems().clear();
        headerArea.clear();
        this.getChildren().remove(headerArea);
    }

    public void setOnNewContentRequest(BiConsumer<Integer, Integer> onContentRequest) {
        this.headerArea.onNewContentRequest = onContentRequest;
    }

    public GoogleBooksTable getTable() {
        return this.table;
    }

    public int getTotalItems() {
        return totalItems.get();
    }

    public IntegerProperty totalItemsProperty() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems.set(totalItems);
    }

    public int getItemsPerPage() {
        return itemsPerPage.get();
    }

    public IntegerProperty itemsPerPageProperty() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage.set(itemsPerPage);
    }

    private static class HeaderArea extends HBox {
        private final IntegerProperty totalItems;
        private final IntegerProperty itemsPerPage;
        private final IntegerProperty pageIndex;
        private final IntegerProperty tableStartIndex;
        private final IntegerProperty pageCount;

        private final Button prevBtn;
        private final Button nextBtn;

        private BiConsumer<Integer, Integer> onNewContentRequest;

        HeaderArea(@NotNull IntegerProperty totalItems,
                   @NotNull IntegerProperty itemsPerPage,
                   @NotNull IntegerProperty tableStartIndex) {
            this.totalItems = totalItems;
            this.itemsPerPage = itemsPerPage;
            this.tableStartIndex = tableStartIndex;
            this.pageIndex = new SimpleIntegerProperty(0);
            this.pageCount = buildPageCountObservable();
            this.prevBtn = buildPrevBtn();
            this.nextBtn = buildNextBtn();
            this.buildUI();
        }

        private IntegerProperty buildPageCountObservable() {
            var pageCount = new SimpleIntegerProperty();
            ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
                pageCount.set((int) Math.ceil(totalItems.doubleValue() / itemsPerPage.doubleValue()));
            };
            listener.changed(null, null, null);
            totalItems.addListener(listener);
            itemsPerPage.addListener(listener);
            return pageCount;
        }

        private Button buildPrevBtn() {
            var btn = new Button(null, new MaterialDesignIconView(MaterialDesignIcon.STEP_BACKWARD));
            btn.setTooltip(new Tooltip(I18N.getValue("google.books.pagination.prev")));
            btn.disableProperty().bind(pageIndex.isEqualTo(0));
            btn.setOnAction(e -> {
                pageIndex.set(pageIndex.get() - 1);
                int firstItemIndex = ((pageIndex.intValue() + 1) * itemsPerPage.intValue()) - itemsPerPage.intValue();
                tableStartIndex.set(firstItemIndex);
                if (onNewContentRequest != null)
                    onNewContentRequest.accept(firstItemIndex, itemsPerPage.intValue());
            });
            return btn;
        }

        private Button buildNextBtn() {
            var btn = new Button(null, new MaterialDesignIconView(MaterialDesignIcon.STEP_FORWARD));
            btn.setTooltip(new Tooltip(I18N.getValue("google.books.pagination.next")));
            btn.disableProperty().bind(pageIndex.add(1).isEqualTo(pageCount));
            btn.setOnAction(e -> {
                pageIndex.set(pageIndex.get() + 1);
                int firstItemIndex = ((pageIndex.intValue() + 1) * itemsPerPage.intValue()) - itemsPerPage.intValue();
                tableStartIndex.set(firstItemIndex);
                if (onNewContentRequest != null)
                    onNewContentRequest.accept(firstItemIndex, itemsPerPage.intValue());
            });
            return btn;
        }

        private Node buildTotalItemsLabel() {
            Label label = new Label();
            label.textProperty().bind(
                    new SimpleStringProperty(I18N.getValue("google.books.pagination.totalitems"))
                            .concat(" : ").concat(totalItems.asString())
            );
            var wrapped = new StackPane(label);
            HBox.setHgrow(wrapped, Priority.ALWAYS);
            return wrapped;
        }

        private Node buildPageLabel() {
            Label label = new Label();
            label.textProperty().bind(pageIndex.add(1).asString().concat("/").concat(pageCount));
            return new StackPane(label);
        }

        private void buildUI() {
            this.setSpacing(10);
            this.getChildren().add(buildTotalItemsLabel());
            this.getChildren().add(prevBtn);
            this.getChildren().add(buildPageLabel());
            this.getChildren().add(nextBtn);
        }

        public void clear() {
            this.onNewContentRequest = null;
            this.pageIndex.set(0);
            this.pageCount.set(0);
            this.itemsPerPage.set(0);
        }
    }
}
