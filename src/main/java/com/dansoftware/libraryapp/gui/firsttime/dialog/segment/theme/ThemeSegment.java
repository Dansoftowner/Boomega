package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.theme;

import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.sgmdialog.TitledSegment;
import javafx.scene.Node;

public class ThemeSegment extends TitledSegment {

    private final Node content;

    public ThemeSegment() {
        super(I18N.getFirstTimeDialogValues().getString("segment.theme.name"),
                I18N.getFirstTimeDialogValues().getString("segment.theme.title"));
        content = loadContent();
    }

    private Node loadContent() {
        return new ImprovedFXMLLoader(
                new Controller(),
                getClass().getResource("ThemeSegment.fxml"),
                I18N.getFirstTimeDialogValues()).load();
    }

    @Override
    protected Node getCenterContent() {
        return content;
    }
}
