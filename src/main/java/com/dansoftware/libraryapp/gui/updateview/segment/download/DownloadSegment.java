package com.dansoftware.libraryapp.gui.updateview.segment.download;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.sgmdialog.TitledSegment;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

/**
 * Allows the user to download the new app from the internet in a {@link com.dansoftware.libraryapp.gui.updateview.UpdateDialog}.
 * The user can select what type of file should be downloaded.
 *
 * @author Daniel Gyorffy
 */
public class DownloadSegment extends TitledSegment {

    private final Context context;
    private final UpdateInformation updateInformation;

    public DownloadSegment(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        super(I18N.getUpdateDialogValues().getString("segment.download.name"),
                I18N.getUpdateDialogValues().getString("segment.download.title"));
        this.context = context;
        this.updateInformation = updateInformation;
    }


    @Override
    protected Node getCenterContent() {
        return new ImprovedFXMLLoader(
                new DownloadSegmentController(context, updateInformation),
                getClass().getResource("DownloadSegment.fxml"),
                I18N.getUpdateDialogValues()).load();
    }
}
