package com.dansoftware.libraryapp.gui.updateview.segment.detail;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Downloads the preview markdown-text about the new update and also renders it, so the user can
 * explore the new features and/or bug fixes in a {@link com.dansoftware.libraryapp.gui.updateview.UpdateDialog}.
 *
 * @author Daniel Gyorffy
 */
public class DetailsSegment extends FixedContentSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public DetailsSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getUpdateDialogValues().getString("segment.details.name"));
        this.context = context;
        this.updateInformation = updateInformation;
    }

    @Override
    protected @NotNull Node createContent() {
        return new ImprovedFXMLLoader(
                new DetailsSegmentController(context, updateInformation),
                getClass().getResource("DetailsSegment.fxml"),
                I18N.getUpdateDialogValues()).load();
    }
}
