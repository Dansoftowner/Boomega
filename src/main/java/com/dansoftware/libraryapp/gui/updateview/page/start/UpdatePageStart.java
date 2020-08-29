package com.dansoftware.libraryapp.gui.updateview.page.start;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.updateview.page.detail.UpdatePageDetail;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * An {@link UpdatePageStart} is an {@link UpdatePage} that is the first page in an {@link UpdateView}.
 *
 * <p>
 * It displays a message on the top that tells the user that a new update is available.
 * Also, it displays what is the current version of the application and what is the version
 * of the new update. It provides two button on the bottom:
 * <ul>
 *     <li>a button that navigates to the next update page ({@link UpdatePageDetail})</li>
 *     <li>a button that simply lets the user to ignore this update</li>
 * </ul>
 *
 * @author Daniel Gyorffy
 * @see UpdatePageDetail
 */
public class UpdatePageStart extends UpdatePage {

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Label nextVersionLabel;

    /**
     * Creates a normal UpdatePageStart object that points to the {@link UpdatePageDetail} as a next
     * update-page.
     *
     * @see UpdatePage#UpdatePage(UpdateView, UpdateInformation, URL)
     * @see UpdatePage#setNextPageFactory(Supplier)
     */
    public UpdatePageStart(@NotNull UpdateView updateView, @NotNull UpdateInformation information) {
        super(updateView, information, UpdatePageStart.class.getResource("UpdatePageStart.fxml"));
        super.setNextPageFactory(() -> new UpdatePageDetail(getUpdateView(), this, getInformation()));
    }

    @FXML
    private void hideUpdateView() {
        getUpdateView().hide();
    }

    @FXML
    private void goToNextPage() {
        super.goNext();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentVersionLabel.setText(Globals.VERSION_INFO.getVersion());
        nextVersionLabel.setText(getInformation().getVersion());
    }
}
