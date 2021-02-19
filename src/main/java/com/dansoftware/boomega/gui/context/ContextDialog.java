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
        return from(workbenchDialog, null);
    }

    @NotNull
    static ContextDialog from(@NotNull WorkbenchDialog workbenchDialog, @Nullable Type type) {
        return new WorkbenchDialogContextDialogAdapter(workbenchDialog, type);
    }
}
