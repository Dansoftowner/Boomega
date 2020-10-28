package com.dansoftware.libraryapp.gui.updateview.segment.detail;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentTitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class DetailsSegment extends FixedContentTitledSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public DetailsSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getUpdateDialogValues().getString("segment.details.name"),
                I18N.getUpdateDialogValues().getString("segment.details.title"));
        this.context = context;
        this.updateInformation = updateInformation;
    }

    @Override
    protected @NotNull Node createCenterContent() {
        return new ImprovedFXMLLoader(
                new DetailsSegmentController(context, updateInformation),
                getClass().getResource("DetailsSegment.fxml"),
                I18N.getUpdateDialogValues()).load();
    }
}
