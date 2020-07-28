package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageStart extends UpdatePage {

    public UpdatePageStart(@NotNull UpdateInformation information) {
        super(information, new FXMLLoader(UpdatePageStart.class.getResource("UpdatePageStart.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
