package com.dansoftware.libraryapp.gui.entry.login.dbcreator;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.locale.Bundles;
import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DatabaseCreatorView extends Workbench {

    private class ModuleImpl extends WorkbenchModule {

        protected ModuleImpl() {
            super("", FontAwesomeIcon.DATABASE);
        }

        @Override
        public Node activate() {
            return DatabaseCreatorView.this.form;
        }
    }

    private final DatabaseCreatorForm form;

    public DatabaseCreatorView() {
        this.init();
        this.form = new DatabaseCreatorForm();
    }

    private void init() {
        this.getToolbarControlsLeft().add(new ToolbarItem(Bundles.getGeneralWord("database.creator.title"),
                new MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS)));
        this.getModules().add(new ModuleImpl());
    }

    public Optional<Account> getCreatedAccount() {
        return this.form.getCreatedAccount();
    }
}
