package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConfigurationImportView} is a GUI object that allows the user to import external settings.
 *
 * @author Daniel Gyorffy
 */
public class ConfigurationImportView extends Workbench implements Themeable {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImportView.class);

    private final ConfigurationImportController controller;
    private final Node content;

    /**
     * Creates a normal {@link ConfigurationImportView}.
     *
     * @param target the object to read the configurations to
     */
    public ConfigurationImportView(@NotNull Preferences target) {
        super();
        this.controller = new ConfigurationImportController(Context.from(this), target);
        this.content = loadContent(controller);
        this.getModules().add(this.new SingleModule());
        Theme.registerThemeable(this);
    }

    private Node loadContent(Initializable controller) {
        return new ImprovedFXMLLoader(
                controller,
                getClass().getResource("ConfigurationImport.fxml"),
                I18N.getFirstTimeDialogValues()
        ).load();
    }

    /**
     * Returns {@code true} if the user imported settings.
     *
     * @return {@code true} if external settings are imported; {@code false} otherwise.
     */
    public boolean externalSettingsImported() {
        return controller.isImported();
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this);
        newTheme.apply(this);
    }

    private final class SingleModule extends WorkbenchModule {
        protected SingleModule() {
            super(StringUtils.EMPTY, (Image) null);
        }

        @Override
        public Node activate() {
            return content;
        }
    }

}
