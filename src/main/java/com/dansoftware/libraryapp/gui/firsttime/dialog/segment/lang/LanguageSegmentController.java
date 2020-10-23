package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.lang;

import com.dansoftware.libraryapp.appdata.Preferences;
import javafx.fxml.Initializable;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class LanguageSegmentController implements Initializable {

    private final Preferences preferences;

    public LanguageSegmentController(@NotNull Preferences preferences) {
        this.preferences = preferences;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
