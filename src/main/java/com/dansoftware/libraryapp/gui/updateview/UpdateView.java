package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePage;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePageDetail;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePageDownload;
import com.dansoftware.libraryapp.gui.updateview.page.UpdatePageStart;
import com.dansoftware.libraryapp.gui.workbench.SimpleHeaderView;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

public class UpdateView extends SimpleHeaderView<UpdatePage> {

    private static final int PAGE_MIN = 0;
    private static final int PAGE_MAX = 2;

    private UpdatePage updatePageStart;
    private UpdatePage updatePageDetail;
    private UpdatePage updatePageDownload;

    private SimpleIntegerProperty currentPageNumberProperty;
    private final Context context;
    private final UpdateInformation information;

    public UpdateView(@NotNull Context context, @NotNull UpdateInformation information) {
        super(I18N.getGeneralWord("update.view.title"), new MaterialDesignIconView(MaterialDesignIcon.UPDATE));
        this.context = context;
        this.information = information;
        this.currentPageNumberProperty = new SimpleIntegerProperty(0);
        this.setContent(getPage(0));
        this.setPrefWidth(500);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setEffect(new DropShadow());
        this.createToolbar();
    }

    private void createToolbar() {
        ToolbarItem prevBtn =
                new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS), event -> goToPrevPage());
        prevBtn.disableProperty().bind(this.currentPageNumberProperty.isEqualTo(0));

        ToolbarItem closeBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.CLOSE), event -> hide());

        this.getToolbarControlsRight().addAll(prevBtn, closeBtn);
    }

    public void goToPrevPage() {
        currentPageNumberProperty.set(currentPageNumberProperty.get() - 1);
        if (currentPageNumberProperty.get() < PAGE_MIN) {
            currentPageNumberProperty.set(PAGE_MIN);
        }

        this.setContent(getPage(currentPageNumberProperty.get()));
    }

    public void goToNextPage() {
        currentPageNumberProperty.set(currentPageNumberProperty.get() + 1);
        if (currentPageNumberProperty.get() > PAGE_MAX) {
            currentPageNumberProperty.set(PAGE_MAX);
        }

        this.setContent(getPage(currentPageNumberProperty.get()));
    }

    public void hide() {
        this.context.hideOverlay((Region) this.getParent().getParent());
    }

    private UpdatePage getPage(int index) {

        switch (index) {
            case 0:
                return updatePageStart == null ?
                        updatePageStart = new UpdatePageStart(this, information) : updatePageStart;
            case 1:
                return updatePageDetail == null ?
                        updatePageDetail = new UpdatePageDetail(this, information) : updatePageDetail;
            case 2:
                return updatePageDownload == null ?
                        updatePageDownload = new UpdatePageDownload(this, information) : updatePageDownload;
        }

        return null;
    }
}
