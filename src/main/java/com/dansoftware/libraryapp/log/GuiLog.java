package com.dansoftware.libraryapp.log;

import com.dansoftware.libraryapp.util.Bundles;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This type of {@link LogRecord} should be used for logging messages if the message
 * should be displayed on the gui for the user
 */
public class GuiLog extends LogRecord {
    /* This block sets the resource bundle for this log record object */ {
        setResourceBundle(Bundles.getExceptionBundle());
    }

    private String title;

    public GuiLog(Level level, String msg) {
        super(level, msg);
    }

    public GuiLog(Level level, String msg, Throwable cause) {
        super(level, msg);
        setThrown(cause);
    }

    public GuiLog(Level level, String title, String msg) {
        this(level, msg);
        this.title = title;
    }

    public GuiLog(Level level, String title, String msg, Throwable cause) {
        this(level, title, msg);
        setThrown(cause);
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }
}
