package com.dansoftware.libraryapp.gui.notification;

import com.dansoftware.libraryapp.locale.Bundles;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.text.MessageFormat;
import java.util.MissingResourceException;

import static com.dansoftware.libraryapp.locale.Bundles.getNotificationMessages;
import static com.dansoftware.libraryapp.util.CommonUtils.isEmpty;
import static java.util.Objects.isNull;

/**
 * A Notification is a data-holder object that can contain all the necessary information
 * about a user-notification.
 *
 * <p>
 * A Notification itself doesn't do anything beyond that it holds the information.
 * The 'heavy job' is done by a {@link NotificationStrategy} object that handles
 * the notification in a particular way.
 *
 * <p>
 * You can define a global NotificationStrategy object with a static method
 * {@link Notification#setStrategy(NotificationStrategy)}.
 *
 * <p>
 * A Notification is localized with the bundle that can be accessed with
 * {@link Bundles#getNotificationMessages()}.
 *
 * @see NotificationStrategy
 */
public class Notification {

    private static NotificationStrategy strategy = new GuiNotificationStrategy();

    private final NotificationLevel level;
    private final String msg;
    private final String title;
    private final Throwable throwable;
    private final Duration visibilityDuration;
    private final EventHandler<ActionEvent> eventHandler;


    private Notification(Builder builder) {
        this.level = builder.level;
        this.msg = getFinalMessage(builder.msgBuilder.getMsg(), builder.msgBuilder.getArgs());
        this.title = getFinalMessage(builder.title);
        this.throwable = builder.throwable;
        this.visibilityDuration = builder.visibilityDuration;
        this.eventHandler = builder.eventHandler;
    }

    /**
     * Creates a Notification-Builder that can be used to build and display the application
     *
     * @return the Builder object
     * @see Builder
     */
    public static Builder create() {
        return new Notification.Builder();
    }

    /**
     * This method returns the localized and formatted message.
     *
     * <p>
     * If no resource found by the given key it will return the key itself.
     *
     * @param msg  the key of the message from the {@link Bundles#getNotificationMessages()}
     * @param args the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     * @return the final message
     */
    private static String getFinalMessage(String msg, Object... args) {
        if (msg == null) return null;

        try {
            var bundle = getNotificationMessages();

            if (isEmpty(args))
                return bundle.getString(msg);
            else
                return MessageFormat.format(bundle.getString(msg), args);

        } catch (MissingResourceException ex) {
            return msg;
        }
    }

    public Duration getVisibilityDuration() {
        return isNull(visibilityDuration) ?
                Duration.INDEFINITE : visibilityDuration;
    }

    public NotificationLevel getLevel() {
        return isNull(level) ?
                NotificationLevel.INFO : level;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public EventHandler<ActionEvent> getEventHandler() {
        return eventHandler;
    }

    public static NotificationStrategy getStrategy() {
        return strategy;
    }

    public static void setStrategy(NotificationStrategy strategy) {
        Notification.strategy = strategy;
    }

    public static final class Builder {
        private NotificationLevel level;
        private MessageBuilder msgBuilder;
        private String title;
        private Throwable throwable;
        private Duration visibilityDuration;
        private EventHandler<ActionEvent> eventHandler;

        public Builder level(NotificationLevel level) {
            this.level = level;
            return this;
        }

        public Builder msg(String msg) {
            this.msgBuilder = MessageBuilder.create(msg);
            return this;
        }

        public Builder msg(MessageBuilder msgBuilder) {
            this.msgBuilder = msgBuilder;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder cause(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder visibilityDuration(Duration visibilityDuration) {
            this.visibilityDuration = visibilityDuration;
            return this;
        }

        public Builder eventHandler(EventHandler<ActionEvent> eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public void show() {
            Notification.strategy.handle(new Notification(this));
        }
    }
}
