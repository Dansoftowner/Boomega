package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.locale.I18N;
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

public class GoogleBooksResultPagination extends VBox {

    private final HeaderArea headerArea;
    private final GoogleBooksSearchResultTable table;

    private final IntegerProperty totalItems;
    private final IntegerProperty itemsPerPage;

    GoogleBooksResultPagination() {
        this.totalItems = new SimpleIntegerProperty();
        this.itemsPerPage = new SimpleIntegerProperty();
        this.table = buildTable();
        this.headerArea = buildHeaderArea();
        this.buildUI();
    }

    private GoogleBooksSearchResultTable buildTable() {
        var table = new GoogleBooksSearchResultTable(0);
        VBox.setVgrow(table, Priority.ALWAYS);
        return table;
    }

    private HeaderArea buildHeaderArea() {
        return new HeaderArea(totalItems, itemsPerPage, table.startIndexProperty());
    }

    private void buildUI() {
        this.getChildren().add(table);
    }

    public void setItems(List<Volume.VolumeInfo> items) {
        if (items != null && !items.isEmpty()) {
            table.getItems().clear();
            table.getItems().addAll(items);
            if (!this.getChildren().contains(headerArea))
                this.getChildren().add(0, headerArea);
        }
    }

    public void clear() {
        this.getChildren().remove(headerArea);
        table.getItems().clear();
        headerArea.onContentRequest = null;
    }

    public void setOnContentRequest(BiConsumer<Integer, Integer> onContentRequest) {
        this.headerArea.onContentRequest = onContentRequest;
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

        private BiConsumer<Integer, Integer> onContentRequest;

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
            btn.setTooltip(new Tooltip(I18N.getGoogleBooksImportValue("google.books.pagination.prev")));
            btn.disableProperty().bind(pageIndex.isEqualTo(0));
            btn.setOnAction(e -> {
                pageIndex.set(pageIndex.get() - 1);
                int firstItemIndex = ((pageIndex.intValue() + 1) * itemsPerPage.intValue()) - itemsPerPage.intValue();
                tableStartIndex.set(firstItemIndex);
                if (onContentRequest != null)
                    onContentRequest.accept(firstItemIndex, itemsPerPage.intValue());
            });
            return btn;
        }

        private Button buildNextBtn() {
            var btn = new Button(null, new MaterialDesignIconView(MaterialDesignIcon.STEP_FORWARD));
            btn.setTooltip(new Tooltip(I18N.getGoogleBooksImportValue("google.books.pagination.next")));
            btn.disableProperty().bind(pageIndex.isEqualTo(pageCount));
            btn.setOnAction(e -> {
                pageIndex.set(pageIndex.get() + 1);
                int firstItemIndex = ((pageIndex.intValue() + 1) * itemsPerPage.intValue()) - itemsPerPage.intValue();
                tableStartIndex.set(firstItemIndex);
                if (onContentRequest != null)
                    onContentRequest.accept(firstItemIndex, itemsPerPage.intValue());
            });
            return btn;
        }

        private Node buildTotalItemsLabel() {
            Label label = new Label();
            label.textProperty().bind(
                    new SimpleStringProperty(I18N.getGoogleBooksImportValue("google.books.pagination.totalitems"))
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

    }
}
