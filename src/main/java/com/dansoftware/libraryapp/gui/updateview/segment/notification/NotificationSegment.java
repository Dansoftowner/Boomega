package com.dansoftware.libraryapp.gui.updateview.segment.notification;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

public class NotificationSegment extends FixedContentSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public NotificationSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getUpdateDialogValues().getString("segment.dialog.start.name"));
        this.context = context;
        this.updateInformation = updateInformation;
    }

    @Override
    protected @NotNull Node createContent() {
        return new ImprovedFXMLLoader(
                new NotificationSegmentController(context, updateInformation),
                getClass().getResource("NotificationSegment.fxml"),
                I18N.getUpdateDialogValues()).load();
    }
}
