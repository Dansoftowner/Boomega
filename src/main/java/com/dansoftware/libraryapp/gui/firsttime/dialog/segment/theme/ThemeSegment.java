package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.theme;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.sgmdialog.TitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class ThemeSegment extends TitledSegment {

    private final Node content;
    private final Preferences preferences;

    public ThemeSegment(@NotNull Preferences preferences) {
        super(I18N.getFirstTimeDialogValues().getString("segment.theme.name"),
                I18N.getFirstTimeDialogValues().getString("segment.theme.title"));
        this.preferences = preferences;
        content = loadContent();
    }

    private Node loadContent() {
        return new ImprovedFXMLLoader(
                new ThemeSegmentController(preferences),
                getClass().getResource("ThemeSegment.fxml"),
                I18N.getFirstTimeDialogValues()).load();
    }

    @Override
    protected Node getCenterContent() {
        return content;
    }
}
