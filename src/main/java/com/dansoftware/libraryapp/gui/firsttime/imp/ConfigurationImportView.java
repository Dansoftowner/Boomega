package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ConfigurationImportView extends Workbench {

    private final Node content;

    public ConfigurationImportView(@NotNull Preferences target) {
        super();
        this.content = loadContent(Context.from(this), target);
        this.getModules().add(this.new Module());
    }

    private Node loadContent(Context context, Preferences target) {
        return new ImprovedFXMLLoader(
                new ConfigurationImportController(context, target),
                getClass().getResource("ConfigurationImport.fxml"),
                I18N.getFirstTimeDialogValues()
        ).load();
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
