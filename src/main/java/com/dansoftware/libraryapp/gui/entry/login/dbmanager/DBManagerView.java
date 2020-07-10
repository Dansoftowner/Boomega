package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import static com.dansoftware.libraryapp.locale.I18N.getGeneralWord;

public class DBManagerView extends SimpleHeaderView {
    public DBManagerView(ObservableList<Account> items) {
        super(getGeneralWord("database.manager.title"), new MaterialDesignIconView(MaterialDesignIcon.DATABASE));
        super.setContent(new DBManagerTable(this, items));
    }
}
