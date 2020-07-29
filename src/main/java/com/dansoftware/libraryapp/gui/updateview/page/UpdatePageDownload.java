package com.dansoftware.libraryapp.gui.updateview.page;

import com.dansoftware.libraryapp.gui.updateview.UpdateView;
import com.dansoftware.libraryapp.update.UpdateInformation;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePageDownload extends UpdatePage {

    public UpdatePageDownload(@NotNull UpdateView updateView, @NotNull UpdatePage previous, @NotNull UpdateInformation information) {
        super(updateView, previous, information, UpdatePageDownload.class.getResource("UpdatePageDownload.fxml"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
