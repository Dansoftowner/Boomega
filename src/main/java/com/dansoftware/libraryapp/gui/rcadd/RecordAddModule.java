package com.dansoftware.libraryapp.gui.rcadd;

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

public class RecordAddModule extends WorkbenchModule {

    private final ObjectProperty<RecordAddForm> content =
            new SimpleObjectProperty<>();

    public RecordAddModule() {
        super(I18N.getRecordAddFormValue("record.add.module.title"), MaterialDesignIcon.PLUS_BOX);
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
            content.set(new RecordAddForm(RecordAddForm.RecordType.BOOK));
        return content.get();
    }

    @Override
    public boolean destroy() {
        this.content.set(null);
        return true;
    }
}
