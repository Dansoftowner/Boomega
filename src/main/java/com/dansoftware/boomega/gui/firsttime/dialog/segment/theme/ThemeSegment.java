package com.dansoftware.boomega.gui.firsttime.dialog.segment.theme;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.sgmdialog.FixedContentTitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible {@link com.dansoftware.sgmdialog.Segment} for choosing the app-theme
 *
 * @author Daniel Gyorffy
 */
public class ThemeSegment extends FixedContentTitledSegment {

    private final Preferences preferences;

    public ThemeSegment(@NotNull Preferences preferences) {
        super(I18N.getValues().getString("segment.theme.name"), I18N.getValues().getString("segment.theme.title"));
        this.preferences = preferences;
    }

    @Override
    protected @NotNull Node createCenterContent() {
        return new ImprovedFXMLLoader(
                new ThemeSegmentController(preferences),
                getClass().getResource("ThemeSegment.fxml"),
                I18N.getValues()).load();
    }
}
