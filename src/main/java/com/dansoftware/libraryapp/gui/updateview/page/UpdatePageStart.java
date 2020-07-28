package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageStart extends UpdatePage {

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Label nextVersionLabel;

    public UpdatePageStart(@NotNull UpdateView updateView, @NotNull UpdateInformation information) {
        super(updateView, information, new FXMLLoader(UpdatePageStart.class.getResource("UpdatePageStart.fxml")));
    }

    @FXML
    private void hideUpdateView() {
        getUpdateView().hide();
    }

    @FXML
    private void goToNextPage() {
        getUpdateView().goToNextPage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentVersionLabel.setText(Globals.VERSION_INFO.getVersion());
        nextVersionLabel.setText(getInformation().getVersion());
    }
}
