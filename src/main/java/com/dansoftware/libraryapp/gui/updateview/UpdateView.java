package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.update.UpdateInformation;
import javafx.fxml.Initializable;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is responsible for showing the update details for the user on the gui.
 * It gives the chance to the user to download the new update.
 *
 * @author Daniel Gyorffy
 */
public class UpdateView //extends SimpleHeaderView {
 implements Initializable {
    private final UpdateInformation info;

    public UpdateView(@NotNull UpdateInformation info) {
        this.info = Objects.requireNonNull(info, "The info mustn't be null");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
