package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.lang;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.firsttime.dialog.segment.FixedSegment;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible {@link com.dansoftware.sgmdialog.Segment} for choosing
 * languages.
 *
 * @author Daniel Gyorffy
 */
public class LanguageSegment extends FixedSegment {

    private final Preferences preferences;

    public LanguageSegment(@NotNull Preferences preferences) {
        super(I18N.getFirstTimeDialogValues().getString("segment.lang.name"), I18N.getFirstTimeDialogValues().getString("segment.lang.title"));
        this.preferences = preferences;
    }

    @Override
    protected Node loadCenterContent() {
        return new ImprovedFXMLLoader(
                new LanguageSegmentController(this.preferences),
                getClass().getResource("LanguageSegment.fxml"),
                I18N.getFirstTimeDialogValues()).load();
    }
}
