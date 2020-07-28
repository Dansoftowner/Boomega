package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageDetail extends UpdatePage {

    public UpdatePageDetail(@NotNull UpdateInformation information) {
        super(information, new FXMLLoader(UpdatePageDetail.class.getResource("UpdatePageDetail.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
