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
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

public class UpdateView extends SimpleHeaderView<UpdatePage> {

    private static final int PAGE_MIN = 0;
    private static final int PAGE_MAX = 2;

    private UpdatePage updatePageStart;
    private UpdatePage updatePageDetail;
    private UpdatePage updatePageDownload;

    private int currentPageNumber;
    private final Context context;
    private final UpdateInformation information;

    public UpdateView(@NotNull Context context, @NotNull UpdateInformation information) {
        super(I18N.getGeneralWord("update.view.title"), new MaterialDesignIconView(MaterialDesignIcon.UPDATE));
        this.context = context;
        this.information = information;
        this.setContent(getPage(0));
        this.setPrefWidth(500);
        this.setPrefHeight(500);
        this.createToolbar();
    }

    private void createToolbar() {
        ToolbarItem prevBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS), event -> {
            currentPageNumber--;
            if (currentPageNumber < PAGE_MIN) {
                currentPageNumber = PAGE_MIN;
            }

            this.setContent(getPage(currentPageNumber));
        });

        ToolbarItem nextBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.SKIP_NEXT), event -> {
            currentPageNumber++;
            if (currentPageNumber > PAGE_MAX) {
                currentPageNumber = PAGE_MAX;
            }

            this.setContent(getPage(currentPageNumber));
        });

        this.getToolbarControlsLeft().addAll(prevBtn, nextBtn);

        ToolbarItem closeBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.CLOSE), event -> {
           this.context.hideOverlay((Region) this.getParent().getParent());
        });

        this.getToolbarControlsRight().add(closeBtn);
    }

    private UpdatePage getPage(int index) {

        switch (index) {
            case 0:
                return updatePageStart == null ?
                        updatePageStart = new UpdatePageStart(information) : updatePageStart;
            case 1:
                return updatePageDetail == null ?
                        updatePageDetail = new UpdatePageDetail(information) : updatePageDetail;
            case 2:
                return updatePageDownload == null ?
                        updatePageDownload = new UpdatePageDownload(information) : updatePageDownload;
        }

        return null;
    }
}
