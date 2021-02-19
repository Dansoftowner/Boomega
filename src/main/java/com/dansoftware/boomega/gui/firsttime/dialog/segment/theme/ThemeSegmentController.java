package com.dansoftware.boomega.gui.firsttime.dialog.segment.theme;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialog;
import com.dansoftware.boomega.gui.theme.DarkTheme;
import com.dansoftware.boomega.gui.theme.LightTheme;
import com.dansoftware.boomega.gui.theme.OsSynchronizedTheme;
import com.dansoftware.boomega.gui.theme.Theme;
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

/**
 * Controller for the theme-segment in the {@link com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialog}.
 *
 * @author Daniel Gyorffy
 */
public class ThemeSegmentController implements Initializable, ChangeListener<Toggle> {

    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioButton darkThemeToggle;

    @FXML
    private RadioButton syncThemeToggle;

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
            Theme.setDefault(darkTheme);
        } else if (lightThemeToggle.equals(newValue)) {
            LightTheme lightTheme = new LightTheme();
            preferences.editor().put(Preferences.Key.THEME, lightTheme).tryCommit();
            Theme.setDefault(lightTheme);
        } else if (syncThemeToggle.equals(newValue)) {
            OsSynchronizedTheme osSynchronizedTheme = new OsSynchronizedTheme();
            preferences.editor().put(Preferences.Key.THEME, osSynchronizedTheme).tryCommit();
            Theme.setDefault(osSynchronizedTheme);
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
