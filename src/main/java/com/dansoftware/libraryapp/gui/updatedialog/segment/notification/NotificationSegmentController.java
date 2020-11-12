package com.dansoftware.libraryapp.gui.updatedialog.segment.notification;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for {@link NotificationSegment}.
 *
 * @author Daniel Gyorffy
 */
public class NotificationSegmentController implements Initializable {

    @FXML
    private Label currentVersionLabel;
    @FXML
    private Label nextVersionLabel;

    private final Context context;
    private final UpdateInformation updateInformation;

    NotificationSegmentController(@NotNull Context context, @NotNull UpdateInformation updateInformation) {
        this.context = Objects.requireNonNull(context);
        this.updateInformation = Objects.requireNonNull(updateInformation);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentVersionLabel.setText(System.getProperty("libraryapp.version"));
        nextVersionLabel.setText(updateInformation.getVersion());
    }
}
