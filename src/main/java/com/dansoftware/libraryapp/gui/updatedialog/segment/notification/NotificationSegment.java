package com.dansoftware.libraryapp.gui.updatedialog.segment.notification;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.i18n.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NotificationSegment extends FixedContentSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public NotificationSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getUpdateDialogValues().getString("segment.dialog.start.name"));
        this.context = Objects.requireNonNull(context);
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
