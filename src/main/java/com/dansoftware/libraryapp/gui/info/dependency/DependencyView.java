package com.dansoftware.libraryapp.gui.info.dependency;

import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.Node;

import java.util.List;

public class DependencyView extends SimpleHeaderView<DependencyTable> {
    public DependencyView() {
        super("Dependency Viewer", new MaterialDesignIconView(MaterialDesignIcon.LANGUAGE_JAVASCRIPT));
        setContent(new DependencyTable(getDependencies()));
    }

    private List<DependencyInfo> getDependencies() {
        return List.of(

        );
    }
}
