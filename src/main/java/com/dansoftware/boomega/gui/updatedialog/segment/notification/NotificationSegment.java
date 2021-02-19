package com.dansoftware.boomega.gui.updatedialog.segment.notification;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NotificationSegment extends FixedContentSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public NotificationSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getValues().getString("segment.dialog.start.name"));
        this.context = Objects.requireNonNull(context);
        this.updateInformation = updateInformation;
    }

    @Override
    protected @NotNull Node createContent() {
        return new ImprovedFXMLLoader(
                new NotificationSegmentController(context, updateInformation),
                getClass().getResource("NotificationSegment.fxml"),
                I18N.getValues()).load();
    }
}
