package com.dansoftware.boomega.gui.firsttime.dialog.segment.lang;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.sgmdialog.FixedContentTitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible {@link com.dansoftware.sgmdialog.Segment} for choosing
 * languages.
 *
 * @author Daniel Gyorffy
 */
public class LanguageSegment extends FixedContentTitledSegment {

    private final Preferences preferences;

    public LanguageSegment(@NotNull Preferences preferences) {
        super(I18N.getValues().getString("segment.lang.name"), I18N.getValues().getString("segment.lang.title"));
        this.preferences = preferences;
    }

    @Override
    protected @NotNull Node createCenterContent() {
        return new LanguageSegmentView(preferences);
    }
}
