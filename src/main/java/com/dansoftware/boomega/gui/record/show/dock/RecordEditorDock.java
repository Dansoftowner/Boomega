package com.dansoftware.boomega.gui.record.show.dock;

import com.dansoftware.boomega.gui.record.edit.RecordEditor;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.SplitPane;
import org.jetbrains.annotations.NotNull;

public class RecordEditorDock extends DockView<RecordEditor> {
    public RecordEditorDock(@NotNull SplitPane parent,
                            @NotNull RecordEditor content) {
        super(
                parent,
                new FontAwesomeIconView(FontAwesomeIcon.EDIT),
                I18N.getValue("record.editor.dock.title"),
                content
        );
    }
}
