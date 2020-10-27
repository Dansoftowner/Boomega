package com.dansoftware.libraryapp.gui.context;

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

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link ContextDialog} is an abstract structure that represents a dialog
 * that is shown through a {@link Context}.
 *
 * @author Daniel Gyorffy
 */
public interface ContextDialog {

    /**
     * The types of a {@link ContextDialog}.
     */
    enum Type {
        INPUT,
        INFORMATION,
        ERROR,
        WARNING,
        CONFIRMATION
    }


    ObjectProperty<EventHandler<Event>> onShownProperty();

    void setOnShown(EventHandler<Event> value);

    EventHandler<Event> getOnShown();

    ObjectProperty<EventHandler<Event>> onHiddenProperty();

    void setOnHidden(EventHandler<Event> value);

    EventHandler<Event> getOnHidden();

    Type getType();

    //

    ObservableList<ButtonType> getButtonTypes();

    //

    BooleanProperty maximizedProperty();

    void setMaximized(boolean max);

    boolean isMaximized();

    //

    ObjectProperty<Node> contentProperty();

    void setContent(Node content);

    Node getContent();

    //

    StringProperty titleProperty();

    String getTitle();

    void setTitle(String title);

    //

    ObservableList<String> getStyleClass();

    //

    ObjectProperty<Exception> exceptionProperty();

    void setException(Exception ex);

    Exception getException();

    //

    String getDetails();

    StringProperty detailsProperty();

    void setDetails(String details);

    //

    BooleanProperty blockingProperty();

    void setBlocking(boolean blocking);

    boolean isBlocking();

    //

    Consumer<ButtonType> getOnResult();

    ObjectProperty<Consumer<ButtonType>> onResultProperty();

    void setOnResult(Consumer<ButtonType> onResult);

    //

    ReadOnlyBooleanProperty showingProperty();

    boolean isShowing();

    /**
     * Wraps a {@link WorkbenchDialog} into a {@link ContextDialog}.
     *
     * @param workbenchDialog the workbench-dialog to wrap
     * @return the {@link ContextDialog} object.
     */
    @NotNull
    static ContextDialog from(@NotNull WorkbenchDialog workbenchDialog) {
        Objects.requireNonNull(workbenchDialog);
        return new ContextDialog() {
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
                return Type.valueOf(workbenchDialog.getType().toString());
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
        };
    }
}
