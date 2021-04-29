package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.record.googlebook.GoogleBookConnectionView;
import com.dansoftware.boomega.gui.record.show.RecordTable;
import com.dansoftware.boomega.i18n.I18N;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

public class GoogleBookConnectionDock extends DockView<GoogleBookConnectionView> {

    public GoogleBookConnectionDock(@NotNull Context context,
                                    @NotNull Database database,
                                    @NotNull SplitPane parent,
                                    @NotNull RecordTable table) {
        super(
                parent,
                new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png")),
                I18N.getValue("google.books.dock.title"),
                buildContent(context, database, table)
        );
    }

    private static GoogleBookConnectionView buildContent(Context context, Database database, RecordTable table) {
        var dockContent = new GoogleBookConnectionView(context, database, table.getSelectionModel().getSelectedItems());
        dockContent.setOnRefreshed(table::refresh);
        table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Record>) change ->
                dockContent.setItems(table.getSelectionModel().getSelectedItems()));
        return dockContent;
    }
}
