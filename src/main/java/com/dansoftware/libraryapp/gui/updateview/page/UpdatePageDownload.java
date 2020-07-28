package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageDownload extends UpdatePage {
    public UpdatePageDownload(@NotNull UpdateInformation information) {
        super(information, new FXMLLoader(UpdatePageDownload.class.getResource("UpdatePageDownload.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
