package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageDownload extends UpdatePage {
    public UpdatePageDownload(@NotNull UpdateView updateView, @NotNull UpdateInformation information) {
        super(updateView, information, new FXMLLoader(UpdatePageDownload.class.getResource("UpdatePageDownload.fxml")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
