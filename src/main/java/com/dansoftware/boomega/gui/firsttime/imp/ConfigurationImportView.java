package com.dansoftware.boomega.gui.firsttime.imp;

import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
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
public class ConfigurationImportView extends Workbench implements ContextTransformable {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImportView.class);

    private final ConfigurationImportController controller;
    private final Node content;

    private final Context asContext;

    /**
     * Creates a normal {@link ConfigurationImportView}.
     *
     * @param target the object to read the configurations to
     */
    public ConfigurationImportView(@NotNull Preferences target) {
        super();
        this.asContext = Context.from(this);
        this.controller = new ConfigurationImportController(asContext, target);
        this.content = loadContent(controller);
        this.getModules().add(this.new SingleModule());
    }

    private Node loadContent(Initializable controller) {
        return new ImprovedFXMLLoader(
                controller,
                getClass().getResource("ConfigurationImport.fxml"),
                I18N.getValues()
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
    public @NotNull Context getContext() {
        return this.asContext;
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
