package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.lang;

import com.dansoftware.libraryapp.gui.firsttime.dialog.segment.FixedSegment;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.Node;

public class LanguageSegment extends FixedSegment {
    public LanguageSegment() {
        super(I18N.getFirstTimeDialogValues().getString("segment.lang.name"), I18N.getFirstTimeDialogValues().getString("segment.lang.title"));
    }

    @Override
    protected Node loadCenterContent() {
        return new ImprovedFXMLLoader(
                new LanguageSegmentController(),
                getClass().getResource("LanguageSegment.fxml"),
                I18N.getFirstTimeDialogValues()).load();
    }
}
