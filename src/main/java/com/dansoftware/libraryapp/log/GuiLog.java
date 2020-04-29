package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.util.Bundles;
import javafx.util.Duration;

import static com.dansoftware.libraryapp.util.Bundles.*;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This type of {@link LogRecord} should be used for logging messages if the message
 * should be displayed on the gui for the user
 *
 * @author Daniel Gyorffy
 */
public class GuiLog extends LogRecord {

    private String title;
    private Duration hideAfterDuration;

    /**
     * Creates a basic GuiLog object with the mandatory elements
     *
     * @param level     the priority level of this log
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     * @see LogRecord#LogRecord(Level, String)
     */
    public GuiLog(Level level, String msg) {
        super(level, getFinalMessage(msg, null));
    }

    /**
     * Creates a basic GuiLog object with the mandatory elements
     *
     * @param level     the priority level of this log
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     * @see LogRecord#LogRecord(Level, String)
     */
    public GuiLog(Level level, String msg, Object[] arguments) {
        super(level, getFinalMessage(msg, arguments));
    }

    /**
     * Creates a basic GuiLog object with an additional information: the {@link Duration} that defines
     * that how long time the notification bar should be showed on the gui for the user
     *
     * @param level             the priority level of this log
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     */
    public GuiLog(Level level, String msg, Duration hideAfterDuration) {
        this(level, msg, (Object[]) null);
        this.hideAfterDuration = hideAfterDuration;
    }

    /**
     * Creates a basic GuiLog object with an additional information: the {@link Duration} that defines
     * that how long time the notification bar should be showed on the gui for the user
     *
     * @param level             the priority level of this log
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     * @param arguments         the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, String msg, Duration hideAfterDuration, Object[] arguments) {
        this(level, msg, arguments);
        this.hideAfterDuration = hideAfterDuration;
    }

    /**
     * Creates a basic GuiLog object but also allows to define that what Throwable is the cause that we logged
     * In most cases this constructor should be used with a {@link Level#SEVERE} or {@link Level#WARNING} level.
     *
     * @param level     the priority level of this log
     * @param cause     the throwable object
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     */
    public GuiLog(Level level, Throwable cause, String msg) {
        this(level, msg, (Object[]) null);
        setThrown(cause);
    }

    /**
     * Creates a basic GuiLog object but also allows to define that what Throwable is the cause that we logged
     * In most cases this constructor should be used with a {@link Level#SEVERE} or {@link Level#WARNING} level.
     *
     * @param level     the priority level of this log
     * @param cause     the throwable object
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, Throwable cause, String msg, Object[] arguments) {
        this(level, msg, arguments);
        setThrown(cause);
    }

    /**
     * Creates a GuiLog object with mandatory elements and a Throwable cause + a Duration
     *
     * @param level             the priority level of this log
     * @param cause             the throwable object
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     */
    public GuiLog(Level level, Throwable cause, String msg, Duration hideAfterDuration) {
        this(level, msg, hideAfterDuration, null);
        setThrown(cause);
    }

    /**
     * Creates a GuiLog object with mandatory elements and a Throwable cause + a Duration
     *
     * @param level             the priority level of this log
     * @param cause             the throwable object
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     * @param arguments         the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, Throwable cause, String msg, Duration hideAfterDuration, Object[] arguments) {
        this(level, msg, hideAfterDuration, arguments);
        setThrown(cause);

    }

    /**
     * Creates a basic GuiLog with mandatory elements and an additional information: the title that defines
     * that what text should be shown on the top of the notification bar
     *
     * @param level     the priority level of this log
     * @param title     the title of the notification bar
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     * @see GuiLog#GuiLog(Level, String, Object...)
     */
    public GuiLog(Level level, String title, String msg, Object[] arguments) {
        this(level, msg, arguments);
        this.title = title;
    }

    /**
     * Creates a GuiLog with mandatory elements + the title that defines
     * that what text should be shown on the top of the notification bar
     * + the {@link Duration} that defines hat how long time the notification bar should
     * be showed on the gui for the user
     *
     * @param level             the priority level of this log
     * @param title             the title of the notification bar
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     * @param arguments         the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     * @see GuiLog#GuiLog(Level, String, Duration, Object...)
     */
    public GuiLog(Level level, String title, String msg, Duration hideAfterDuration, Object[] arguments) {
        this(level, msg, hideAfterDuration, arguments);
        this.title = title;
    }

    /**
     * Creates a GuiLog with mandatory elements + a Throwable cause and title that defines
     * that what text should be shown on the top of the notification bar
     *
     * @param level     the priority level of this log
     * @param cause     the throwable object
     * @param title     the title of the notification bar
     * @param msg       the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, Throwable cause, String title, String msg, Object[] arguments) {
        this(level, title, msg, arguments);
        setThrown(cause);
    }

    /**
     * Creates a GuiLog with mandatory elements + a Throwable cause and title that defines
     * that what text should be shown on the top of the notification bar + the duration that defines
     * that how long time the notification bar should be showed on the gui for the user
     *
     * @param level             the priority level of this log
     * @param cause             the throwable object
     * @param title             the title of the notification bar
     * @param msg               the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     * @param arguments         the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, Throwable cause, String title, String msg, Duration hideAfterDuration, Object[] arguments) {
        this(level, title, msg, hideAfterDuration, arguments);
        setThrown(cause);
    }

    /**
     * This method returns the localized and formatted message
     *
     * @param msg  the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param args the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     * @return the final message
     */
    private static String getFinalMessage(String msg, Object[] args) {
        if (isEmpty(args))
            return getExceptionBundle().getString(msg);

        return MessageFormat.format(getExceptionBundle().getString(msg), args);
    }

    /**
     * With this method we can decide that an array is empty or not.
     *
     * @param array the array to be checked
     * @return <code>true</code> - if the array is empty <code>false</code> - otherwise
     */
    private static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Duration getHideAfterDuration() {
        return hideAfterDuration;
    }
}
