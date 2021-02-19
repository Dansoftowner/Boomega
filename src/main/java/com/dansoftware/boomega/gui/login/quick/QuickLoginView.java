package com.dansoftware.boomega.gui.login.quick;

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.login.DatabaseLoginListener;
import com.dansoftware.boomega.gui.util.ImprovedFXMLLoader;
import com.dansoftware.boomega.i18n.I18N;
import com.dlsc.workbenchfx.SimpleHeaderView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

public class QuickLoginView extends SimpleHeaderView<Region> implements ContextTransformable {

    private final Context asContext;

    public QuickLoginView(@NotNull DatabaseMeta databaseMeta, @NotNull DatabaseLoginListener loginListener) {
        super(buildTitle(databaseMeta), new MaterialDesignIconView(MaterialDesignIcon.LOGIN));
        this.asContext = Context.from(this);
        this.setContent(loadContent(databaseMeta, loginListener));
    }

    private VBox loadContent(@NotNull DatabaseMeta databaseMeta, @NotNull DatabaseLoginListener loginListener) {
        return new ImprovedFXMLLoader(
                new QuickFormController(asContext, databaseMeta, loginListener),
                getClass().getResource("Form.fxml"), I18N.getValues()).load();
    }

    private static String buildTitle(@NotNull DatabaseMeta databaseMeta) {
        return String.format("%s - %s", I18N.getValue("login.quick.title"), databaseMeta);
    }

    @Override
    public @NotNull Context getContext() {
        return asContext;
    }
}
