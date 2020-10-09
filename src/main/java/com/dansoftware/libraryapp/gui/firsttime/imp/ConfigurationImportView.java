package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.Context;
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

public class ConfigurationImportView extends Workbench implements Themeable {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImportView.class);

    private final ConfigurationImportController controller;
    private final Node content;

    public ConfigurationImportView(@NotNull Preferences target) {
        super();
        this.controller = new ConfigurationImportController(Context.from(this), target);
        this.content = loadContent(controller);
        this.getModules().add(this.new Module());
        Theme.registerThemeable(this);
    }

    private Node loadContent(Initializable controller) {
        return new ImprovedFXMLLoader(
                controller,
                getClass().getResource("ConfigurationImport.fxml"),
                I18N.getFirstTimeDialogValues()
        ).load();
    }

    public ConfigurationImportController getController() {
        return controller;
    }

    @Override
    public void handleThemeApply(Theme newTheme) {
        newTheme.apply(this);
    }

    private final class Module extends WorkbenchModule {

        protected Module() {
            super(StringUtils.EMPTY, (Image) null);
        }

        @Override
        public Node activate() {
            return content;
        }
    }

}
