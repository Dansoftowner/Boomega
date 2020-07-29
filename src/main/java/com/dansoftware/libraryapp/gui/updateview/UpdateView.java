package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePageStart;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

public class UpdateView extends SimpleHeaderView<UpdatePage> implements ChangeListener<UpdatePage> {

    private final ObjectProperty<@NotNull UpdatePage> updatePageProperty;
    private final Context context;
    private final UpdateInformation information;

    private final ToolbarItem prevBtn;
    private final ToolbarItem closeBtn;
    private final ToolbarItem reloadBtn;

    public UpdateView(@NotNull Context context, @NotNull UpdateInformation information) {
        super(I18N.getGeneralWord("update.view.title"), new MaterialDesignIconView(MaterialDesignIcon.UPDATE));
        this.context = context;
        this.information = information;
        this.updatePageProperty = new SimpleObjectProperty<>();
        this.updatePageProperty.addListener(this);
        this.prevBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS), event -> {});
        this.closeBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.CLOSE), event -> hide());
        this.reloadBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.RELOAD), event -> getUpdatePage().reload());

        this.getToolbarControlsRight().addAll(prevBtn, reloadBtn, closeBtn);
        this.setUpdatePage(new UpdatePageStart(this, information));
        this.setPrefWidth(500);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setEffect(new DropShadow());
    }

    public void hide() {
        this.context.hideOverlay((Region) this.getParent().getParent());
    }

    public void setUpdatePage(@NotNull UpdatePage updatePage) {
        this.updatePageProperty.set(updatePage);
    }

    public ToolbarItem getPrevBtn() {
        return prevBtn;
    }

    public ToolbarItem getCloseBtn() {
        return closeBtn;
    }

    public ToolbarItem getReloadBtn() {
        return reloadBtn;
    }

    public Context getContext() {
        return context;
    }

    public @NotNull UpdatePage getUpdatePage() {
        return updatePageProperty.get();
    }

    @Override
    public void changed(ObservableValue<? extends UpdatePage> observable, UpdatePage oldPage, UpdatePage newPage) {
        setContent(newPage);
        newPage.onFocus(this);
    }
}
