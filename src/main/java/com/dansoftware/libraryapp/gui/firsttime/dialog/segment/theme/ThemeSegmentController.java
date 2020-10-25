package com.dansoftware.libraryapp.gui.firsttime.dialog.segment.theme;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialog;
import com.dansoftware.libraryapp.gui.theme.DarkTheme;
import com.dansoftware.libraryapp.gui.theme.LightTheme;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemeSegmentController implements Initializable, ChangeListener<Toggle> {

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioButton darkThemeToggle;

    @FXML
    private RadioButton lightThemeToggle;

    private final Preferences preferences;

    public ThemeSegmentController(@NotNull Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (darkThemeToggle.equals(newValue)) {
            DarkTheme darkTheme = new DarkTheme();
            preferences.editor().put(Preferences.Key.THEME, darkTheme).tryCommit();
            getParentDialog().handleThemeApply(darkTheme);
        } else if (lightThemeToggle.equals(newValue)) {
            LightTheme lightTheme = new LightTheme();
            preferences.editor().put(Preferences.Key.THEME, lightTheme).tryCommit();
            getParentDialog().handleThemeApply(lightTheme);
        }
    }

    private FirstTimeDialog getParentDialog() {
        return ((FirstTimeDialog) darkThemeToggle.getScene().getRoot());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeGroup.selectedToggleProperty().addListener(this);
    }
}
