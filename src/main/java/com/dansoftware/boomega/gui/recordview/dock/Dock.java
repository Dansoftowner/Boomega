package com.dansoftware.boomega.gui.recordview.dock;

import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.recordview.connection.GoogleBookConnectionDock;
import com.dansoftware.boomega.gui.recordview.edit.RecordEditorDock;
import com.dansoftware.boomega.gui.recordview.RecordTable;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum Dock {

    RECORD_EDITOR(RecordEditorDock.class, "record.editor.dock.title") {
        @Override
        protected DockView<?> buildView(@NotNull Context context,
                                        @NotNull Database database,
                                        @NotNull RecordTable table,
                                        @NotNull SplitPane splitPane) {
            return new RecordEditorDock(context, database, splitPane, table);
        }

        @Override
        public Node getGraphic() {
            return new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        }
    },

    GOOGLE_BOOK_CONNECTION(GoogleBookConnectionDock.class,"google.books.dock.title") {
        @Override
        protected DockView<?> buildView(@NotNull Context context,
                                        @NotNull Database database,
                                        @NotNull RecordTable table,
                                        @NotNull SplitPane splitPane) {

            return new GoogleBookConnectionDock(context, database, splitPane, table);
        }

        @Override
        public Node getGraphic() {
            return new MaterialDesignIconView(MaterialDesignIcon.GOOGLE);
        }
    };

    private final Class<? extends DockView<?>> dockViewClass;
    private final String i18n;

    Dock(@NotNull Class<? extends DockView<?>> dockViewClass, @NotNull String i18n) {
        this.dockViewClass = dockViewClass;
        this.i18n = i18n;
    }

    public String getI18nKey() {
        return i18n;
    }

    protected abstract DockView<?> buildView(
            @NotNull Context context,
            @NotNull Database database,
            @NotNull RecordTable table,
            @NotNull SplitPane splitPane
    );

    public final void align(@NotNull Context context,
                      @NotNull Database database,
                      @NotNull RecordTable table,
                      @NotNull SplitPane splitPane) {
        if (splitPane.getItems().stream().noneMatch(dockViewClass::isInstance)) {
            splitPane.getItems().add(buildView(context, database, table, splitPane));
        }
    }

    public final void removeFrom(@NotNull SplitPane splitPane) {
        splitPane.getItems().removeIf(dockViewClass::isInstance);
    }

    /**
     * @return the icon of the dock
     */
    public abstract Node getGraphic();

    @Nullable
    public static Dock parse(Class<? extends DockView<?>> dockViewClass) {
        return Arrays.stream(values()).filter(it -> it.dockViewClass.equals(dockViewClass)).findFirst().orElse(null);
    }
}
