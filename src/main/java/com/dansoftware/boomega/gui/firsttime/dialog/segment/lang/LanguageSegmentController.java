package com.dansoftware.boomega.gui.firsttime.dialog.segment.lang;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.i18n.I18N;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the language-segment in the {@link com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialog}.
 *
 * @author Daniel Gyorffy
 */
public class LanguageSegmentController
        implements Initializable, ChangeListener<LanguageSegmentController.LanguageEntry> {

    /**
     * Represents an item in the ListView
     */
    static final class LanguageEntry {

        private final Locale locale;

        private LanguageEntry(Locale locale) {
            this.locale = locale;
        }

        @Override
        public String toString() {
            return locale.getDisplayName();
        }
    }

    @FXML
    private ListView<LanguageEntry> listView;

    private final Preferences preferences;

    public LanguageSegmentController(@NotNull Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void changed(ObservableValue<? extends LanguageEntry> observable, LanguageEntry oldValue, LanguageEntry newValue) {
        if (newValue != null) {
            preferences.editor().put(Preferences.Key.LOCALE, newValue.locale);
            Locale.setDefault(newValue.locale);
        } else listView.getSelectionModel().select(oldValue); //we don't allow the user to choose no items
    }

    private void fillListView(ListView<LanguageEntry> listView) {
        List<Locale> availableLocales = new ArrayList<>(I18N.getAvailableLocales());
        int defaultLocaleIndex = availableLocales.indexOf(Locale.getDefault());

        listView.getItems().addAll(I18N.getAvailableLocales().stream()
                .map(LanguageEntry::new)
                .collect(Collectors.toList()));
        listView.getSelectionModel().select(defaultLocaleIndex);
        listView.scrollTo(defaultLocaleIndex);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillListView(listView);
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener(this);
    }
}
