package com.dansoftware.libraryapp.gui.info.dependency;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public class DependencyView extends SimpleHeaderView<DependencyTable>
        implements Themeable {
    public DependencyView() {
        super("Dependency Viewer", new MaterialDesignIconView(MaterialDesignIcon.LANGUAGE_JAVASCRIPT));
        setContent(new DependencyTable(DependencyLister.getDependencies()));
        Theme.registerThemeable(this);
    }


    @Override
    public void handleThemeApply(Theme newTheme) {
        newTheme.apply(this);
    }
}
