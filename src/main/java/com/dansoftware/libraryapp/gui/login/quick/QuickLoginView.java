package com.dansoftware.libraryapp.gui.login.quick;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.login.DatabaseLoginListener;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.gui.util.ImprovedFXMLLoader;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class QuickLoginView extends SimpleHeaderView<Region> implements ContextTransformable, Themeable {

    private final Context asContext;

    public QuickLoginView(@NotNull DatabaseMeta databaseMeta, @NotNull DatabaseLoginListener loginListener) {
        super(buildTitle(databaseMeta), new MaterialDesignIconView(MaterialDesignIcon.LOGIN));
        this.asContext = Context.from(this);
        this.setContent(loadContent(databaseMeta, loginListener));
        Theme.registerThemeable(this);
    }

    private VBox loadContent(@NotNull DatabaseMeta databaseMeta, @NotNull DatabaseLoginListener loginListener) {
        return new ImprovedFXMLLoader(
                new QuickFormController(asContext, databaseMeta, loginListener),
                getClass().getResource("Form.fxml"), I18N.getLoginViewValues()).load();
    }


    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this);
        newTheme.apply(this);
    }

    private static String buildTitle(@NotNull DatabaseMeta databaseMeta) {
        return String.format("%s - %s", I18N.getLoginViewValue("login.quick.title"), databaseMeta);
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
