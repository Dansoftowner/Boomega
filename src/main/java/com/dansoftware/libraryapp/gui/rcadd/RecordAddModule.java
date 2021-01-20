package com.dansoftware.libraryapp.gui.rcadd;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.Magazine;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecordAddModule extends WorkbenchModule {

    private final ObjectProperty<RecordAddForm> content =
            new SimpleObjectProperty<>();

    private final Context context;
    private final Database database;

    public RecordAddModule(@NotNull Context context,
                           @NotNull Database database) {
        super(I18N.getRecordAddFormValue("record.add.module.title"), MaterialDesignIcon.PLUS_BOX);
        this.context = context;
        this.database = database;
        buildToolbar();
    }

    private void buildToolbar() {
        this.getToolbarControlsLeft().add(buildRecordTypeChooserItem());
    }

    private ToolbarItem buildRecordTypeChooserItem() {
        var toolbarItem = new ToolbarItem();
        var toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                toolbarItem.setText(((MenuItem) newValue).getText())
        );
        toolbarItem.getItems().add(new RadioMenuItem(I18N.getRecordAddFormValue("record.add.rectype.book")) {{
            content.addListener((observable, oldValue, newValue) -> {
                if (newValue != null)
                    setSelected(RecordAddForm.RecordType.BOOK.equals(newValue.recordTypeProperty().get()));
            });
            this.setToggleGroup(toggleGroup);
            this.setOnAction(event -> content.get().recordTypeProperty().set(RecordAddForm.RecordType.BOOK));
        }});
        toolbarItem.getItems().add(new RadioMenuItem(I18N.getRecordAddFormValue("record.add.rectype.magazine")) {{
            content.addListener((observable, oldValue, newValue) -> {
                if (newValue != null)
                    setSelected(RecordAddForm.RecordType.MAGAZINE.equals(newValue.recordTypeProperty().get()));
            });
            this.setToggleGroup(toggleGroup);
            this.setOnAction(event -> content.get().recordTypeProperty().set(RecordAddForm.RecordType.MAGAZINE));
        }});
        return toolbarItem;
    }

    @Override
    public Node activate() {
        if (content.get() == null)
            content.set(buildForm());
        return content.get();
    }

    @Override
    public boolean destroy() {
        this.content.set(null);
        return true;
    }

    private RecordAddForm buildForm() {
        var form = new RecordAddForm(context, RecordAddForm.RecordType.BOOK);
        form.setOnBookAdded(buildBookAddAction());
        form.setOnMagazineAdded(buildMagazineAddAction());
        return form;
    }

    private Consumer<Book> buildBookAddAction() {
        return book -> {
            try {
                database.insertBook(book);
                context.showInformationNotification(
                        I18N.getRecordAddFormValue("record.book.success.notification"),
                        null,
                        Duration.millis(5000)
                );
            } catch (RuntimeException e) {
                context.showErrorDialog(
                        I18N.getRecordAddFormValue("record.book.error.title"),
                        I18N.getRecordAddFormValue("record.book.error.msg"), e
                );
            }
        };
    }

    private Consumer<Magazine> buildMagazineAddAction() {
        return magazine -> {
            try {
                database.insertMagazine(magazine);
                context.showInformationNotification(
                        I18N.getRecordAddFormValue("record.magazine.success.notification"),
                        null,
                        Duration.millis(5000)
                );
            } catch (RuntimeException e) {
                context.showErrorDialog(
                        I18N.getRecordAddFormValue("record.magazine.error.title"),
                        I18N.getRecordAddFormValue("record.magazine.error.msg"), e
                );
            }
        };
    }
}
