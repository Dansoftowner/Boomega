/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.context;

import com.dlsc.workbenchfx.model.WorkbenchDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Wraps a {@link WorkbenchDialog} into a {@link ContextDialog} implementation.
 *
 * @author Daniel Gyorffy
 */
class WorkbenchDialogContextDialogAdapter implements ContextDialog {

    private static final String DIALOG_STYLE_CLASS = "alertDialog";

    private final WorkbenchDialog workbenchDialog;
    private final Type type;

    WorkbenchDialogContextDialogAdapter(@NotNull WorkbenchDialog workbenchDialog, @Nullable Type type) {
        this.workbenchDialog = Objects.requireNonNull(workbenchDialog);
        this.workbenchDialog.getStyleClass().add(DIALOG_STYLE_CLASS);
        this.type = workbenchDialog.getType() != null ? Type.valueOf(workbenchDialog.getType().toString()) : type;
    }

    @Override
    public ObjectProperty<EventHandler<Event>> onShownProperty() {
        return workbenchDialog.onShownProperty();
    }

    @Override
    public void setOnShown(EventHandler<Event> value) {
        workbenchDialog.setOnShown(value);
    }

    @Override
    public EventHandler<Event> getOnShown() {
        return workbenchDialog.getOnShown();
    }

    @Override
    public ObjectProperty<EventHandler<Event>> onHiddenProperty() {
        return workbenchDialog.onHiddenProperty();
    }

    @Override
    public void setOnHidden(EventHandler<Event> value) {
        workbenchDialog.setOnHidden(value);
    }

    @Override
    public EventHandler<Event> getOnHidden() {
        return workbenchDialog.getOnHidden();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public ObservableList<ButtonType> getButtonTypes() {
        return workbenchDialog.getButtonTypes();
    }

    @Override
    public BooleanProperty maximizedProperty() {
        return workbenchDialog.maximizedProperty();
    }

    @Override
    public void setMaximized(boolean max) {
        workbenchDialog.setMaximized(max);
    }

    @Override
    public boolean isMaximized() {
        return workbenchDialog.isMaximized();
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return workbenchDialog.contentProperty();
    }

    @Override
    public void setContent(Node content) {
        workbenchDialog.setContent(content);
    }

    @Override
    public Node getContent() {
        return workbenchDialog.getContent();
    }

    @Override
    public StringProperty titleProperty() {
        return workbenchDialog.titleProperty();
    }

    @Override
    public String getTitle() {
        return workbenchDialog.getTitle();
    }

    @Override
    public void setTitle(String title) {
        workbenchDialog.setTitle(title);
    }

    @Override
    public ObservableList<String> getStyleClass() {
        return workbenchDialog.getStyleClass();
    }

    @Override
    public ObjectProperty<Exception> exceptionProperty() {
        return workbenchDialog.exceptionProperty();
    }

    @Override
    public void setException(Exception ex) {
        workbenchDialog.setException(ex);
    }

    @Override
    public Exception getException() {
        return workbenchDialog.getException();
    }

    @Override
    public String getDetails() {
        return workbenchDialog.getDetails();
    }

    @Override
    public StringProperty detailsProperty() {
        return workbenchDialog.detailsProperty();
    }

    @Override
    public void setDetails(String details) {
        workbenchDialog.setDetails(details);
    }

    @Override
    public BooleanProperty blockingProperty() {
        return workbenchDialog.blockingProperty();
    }

    @Override
    public void setBlocking(boolean blocking) {
        workbenchDialog.setBlocking(blocking);
    }

    @Override
    public boolean isBlocking() {
        return workbenchDialog.isBlocking();
    }

    @Override
    public Consumer<ButtonType> getOnResult() {
        return workbenchDialog.getOnResult();
    }

    @Override
    public ObjectProperty<Consumer<ButtonType>> onResultProperty() {
        return workbenchDialog.onResultProperty();
    }

    @Override
    public void setOnResult(Consumer<ButtonType> onResult) {
        workbenchDialog.setOnResult(onResult);
    }

    @Override
    public ReadOnlyBooleanProperty showingProperty() {
        return workbenchDialog.showingProperty();
    }

    @Override
    public boolean isShowing() {
        return workbenchDialog.isShowing();
    }
}
