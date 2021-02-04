package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Record;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.record.show.dock.googlebook.GoogleBookDockContent;
import com.dansoftware.libraryapp.i18n.I18N;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecordsView extends SplitPane {

    private final Context context;
    private final Database database;
    private final RecordTable recordTable;

    private final SplitPane dockSplitPane;

    RecordsView(@NotNull Context context, @NotNull Database database) {
        this.context = context;
        this.database = database;
        this.recordTable = buildBooksTable();
        this.dockSplitPane = buildDockSplitPane();
        this.setOrientation(Orientation.HORIZONTAL);
        this.buildUI();
    }

    private SplitPane buildDockSplitPane() {
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setPrefWidth(500);
        splitPane.setMaxWidth(500);
        splitPane.getItems().add(buildGoogleBooksDock());
        SplitPane.setResizableWithParent(splitPane, false);
        return splitPane;
    }

    private RecordTable buildBooksTable() {
        return new RecordTable(0);
    }

    private void buildUI() {
        this.getItems().addAll(recordTable, dockSplitPane);
    }

    private Node buildGoogleBooksDock() {
        var dockContent = new GoogleBookDockContent(context, database, recordTable.getSelectionModel().getSelectedItems());
        this.recordTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change -> {
            dockContent.setItems(this.recordTable.getSelectionModel().getSelectedItems());
        });
        return new GoogleBookDock(dockContent);
    }

    public void setDockFullyResizable() {
        dockSplitPane.setMaxWidth(Region.USE_COMPUTED_SIZE);
    }

    public void scrollToTop() {
        this.recordTable.scrollTo(0);
    }

    public void setItems(@NotNull List<Record> items) {
        this.recordTable.getItems().setAll(items);
    }

    public void setStartIndex(int startIndex) {
        this.recordTable.startIndexProperty().set(startIndex);
    }

    public RecordTable getBooksTable() {
        return recordTable;
    }

    private static abstract class TitledDock<T extends Node> extends VBox {

        private final T content;

        TitledDock(@NotNull Node icon,
                   @NotNull String title,
                   @NotNull T content) {
            this.content = content;
            this.buildUI(icon, title);
        }

        private void buildUI(Node icon, String title) {
            SplitPane.setResizableWithParent(this, false);
            VBox.setVgrow(content, Priority.ALWAYS);
            getStyleClass().add("titled-dock");
            getChildren().add(buildTitleBar(icon, title));
            getChildren().add(content);
        }

        private Node buildTitleBar(Node icon, String title) {
            var hBox = new HBox(10.0, new StackPane(icon), new Label(title));

            hBox.getStyleClass().add("dock-title-bar");
            return hBox;
        }
    }

    private static final class GoogleBookDock extends TitledDock<GoogleBookDockContent> {

        private final GoogleBookDockContent content;

        GoogleBookDock(@NotNull GoogleBookDockContent content) {
            super(new ImageView(new Image("/com/dansoftware/libraryapp/image/util/google_12px.png")),
                    I18N.getGoogleBooksValue("google.books.dock.title"),
                    content);
            this.content = content;
        }
    }

}
