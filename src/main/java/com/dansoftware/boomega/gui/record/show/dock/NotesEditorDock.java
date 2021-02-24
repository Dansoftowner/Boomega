package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.gui.record.edit.NotesEditor;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.SplitPane;
import javafx.scene.web.HTMLEditor;
import org.jetbrains.annotations.NotNull;

public class NotesEditorDock extends DockView<NotesEditor> {

    public NotesEditorDock(@NotNull SplitPane parent, @NotNull NotesEditor content) {
        //TODO: i18n title
        super(parent, new MaterialDesignIconView(MaterialDesignIcon.NOTE), "Notes Editor", content);
    }
}
