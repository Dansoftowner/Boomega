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
import org.jetbrains.annotations.NotNull;

/**
 * An UpdateView is a GUI object that can show a user an update-dialog.
 *
 * <p>
 * It needs a {@link Context} that gives the ability to show error-dialogs etc...
 * It needs a {@link HideStrategy} that defines how to hide exactly the {@link UpdateView}
 * when the user clicks on the close-toolbar-item.
 *
 * <p>
 * It's using {@link UpdatePage} objects for displaying the actual content.
 * The first {@link UpdatePage} that is displayed is {@link UpdatePageStart}.
 *
 * <p>
 * It has three toolbar-items:
 * <ul>
 *     <li>a "previous"-button that can navigate to the previous {@link UpdatePage}</li>
 *     <li>a "close"-button that hides the whole {@link UpdateView} from the {@link Context}</li>
 *     <li>
 *         a "reload"-button that "refreshes" the current {@link UpdatePage}
 *         (it depends on what is defined in the update-page's {@link UpdatePage#reload()} method.)
 *     </li>
 * </ul>
 *
 * @author Daniel Gyorffy
 */
public class UpdateView extends SimpleHeaderView<UpdatePage> implements ChangeListener<UpdatePage> {

    /**
     * A HideStrategy defines how to close the particular {@link UpdateView}.
     */
    public interface HideStrategy {
        void hide(@NotNull Context context, @NotNull UpdateView view);
    }

    private final ObjectProperty<@NotNull UpdatePage> updatePageProperty;
    private final Context context;
    private final HideStrategy hideStrategy;
    private final UpdateInformation information;

    private final ToolbarItem prevBtn;
    private final ToolbarItem closeBtn;
    private final ToolbarItem reloadBtn;

    /**
     * Creates a normal UpdateView with all necessary data.
     *
     * @param context the context that defines that where to show this update-dialog
     * @param hideStrategy the HideStrategy object that defines how to hide the
     * @param information the object that holds all information about the update
     */
    public UpdateView(@NotNull Context context,
                      @NotNull HideStrategy hideStrategy,
                      @NotNull UpdateInformation information) {
        super(I18N.getGeneralWord("update.view.title"), new MaterialDesignIconView(MaterialDesignIcon.UPDATE));
        this.context = context;
        this.hideStrategy = hideStrategy;
        this.information = information;

        //creating the ObservableValue that represents the current updatePage
        this.updatePageProperty = new SimpleObjectProperty<>();
        this.updatePageProperty.addListener(this);

        //creating the toolbar buttons
        this.prevBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.SKIP_PREVIOUS), event -> {});
        this.closeBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.CLOSE), event -> hide());
        this.reloadBtn = new ToolbarItem(new MaterialDesignIconView(MaterialDesignIcon.RELOAD), event -> getUpdatePage().reload());
        //adding the toolbar items
        this.getToolbarControlsRight().addAll(prevBtn, reloadBtn, closeBtn);

        //setting the first UpdatePage object
        this.setUpdatePage(new UpdatePageStart(this, information));

        this.setPrefWidth(500);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setEffect(new DropShadow());
    }

    public void hide() {
        this.hideStrategy.hide(this.context, this);
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

    @NotNull
    public UpdatePage getUpdatePage() {
        return updatePageProperty.get();
    }

    @Override
    public void changed(ObservableValue<? extends UpdatePage> observable, UpdatePage oldPage, UpdatePage newPage) {
        setContent(newPage);
        newPage.onFocus(this);
    }
}
