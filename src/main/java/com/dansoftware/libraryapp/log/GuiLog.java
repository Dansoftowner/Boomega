package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.util.Bundles;

import static com.dansoftware.libraryapp.util.Bundles.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This type of {@link LogRecord} should be used for logging messages if the message
 * should be displayed on the gui for the user
 */
public class GuiLog extends LogRecord {

    private String title;
    private Duration hideAfterDuration;

    /**
     * Creates a basic GuiLog object with the mandatory elements
     *
     * @param level the priority level of this log
     * @param msg the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, String msg, Object... arguments) {
        super(level, getFinalMessage(msg, arguments));
    }

    /**
     * Creates a basic GuiLog object with an additional information: the {@link Duration} that defines
     * that how long time the notification bar should be showed on the gui for the user
     *
     * @param level the priority level of this log
     * @param msg the key of the message from the {@link Bundles#getExceptionBundle()}
     * @param hideAfterDuration the duration that defines how long time should be the Notification bar showed
     * @param arguments the dynamic values that are should be placed into the messages by the {@link MessageFormat}
     */
    public GuiLog(Level level, String msg, Duration hideAfterDuration, Object... arguments) {
        this(level, msg, arguments);
        this.hideAfterDuration = hideAfterDuration;
    }

    /**
     *
     * @param level
     * @param cause
     * @param msg
     * @param arguments
     */
    public GuiLog(Level level, Throwable cause, String msg, Object... arguments) {
        this(level, msg, arguments);
        setThrown(cause);
    }

    public GuiLog(Level level, Throwable cause, String msg, Duration hideAfterDuration, Object... arguments) {
        this(level, msg, hideAfterDuration, arguments);
        setThrown(cause);

    }

    public GuiLog(Level level, String title, String msg, Object... arguments) {
        this(level, msg, arguments);
        this.title = title;
    }

    public GuiLog(Level level, Throwable cause, String title, String msg, Object... arguments) {
        this(level, title, msg, arguments);
        setThrown(cause);
    }

    private static String getFinalMessage(String msg, Object... args) {
        if (isEmpty(args))
            return getExceptionBundle().getString(msg);

        return MessageFormat.format(getExceptionBundle().getString(msg), args);
    }

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
