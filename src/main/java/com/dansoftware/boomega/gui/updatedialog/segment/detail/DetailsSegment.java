package com.dansoftware.boomega.gui.updatedialog.segment.detail;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.update.UpdateInformation;
import com.dansoftware.sgmdialog.FixedContentSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Downloads the preview markdown-text about the new update and also renders it, so the user can
 * explore the new features and/or bug fixes in a {@link com.dansoftware.boomega.gui.updatedialog.UpdateDialog}.
 *
 * @author Daniel Gyorffy
 */
public class DetailsSegment extends FixedContentSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public DetailsSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getValues().getString("segment.details.name"));
        this.context = context;
        this.updateInformation = updateInformation;
    }

    @Override
    protected @NotNull Node createContent() {
        return new DetailsSegmentView(context, updateInformation);
    }
}
