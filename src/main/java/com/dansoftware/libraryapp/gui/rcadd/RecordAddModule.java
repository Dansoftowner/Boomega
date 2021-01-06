package com.dansoftware.libraryapp.gui.rcadd;

import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;

public class RecordAddModule extends WorkbenchModule {
    public RecordAddModule() {
        super(I18N.getRecordAddFormValue("record.add.module.title"), MaterialDesignIcon.PLUS_BOX);
    }

    @Override
    public Node activate() {
        return new RecordAddForm(RecordAddForm.RecordType.BOOK);
    }
}
