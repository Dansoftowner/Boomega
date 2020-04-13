package com.dansoftware.libraryapp.log;

import java.time.Clock;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class GuiLog extends LogRecord {

    public GuiLog(Level level, String msg) {
        super(level, msg);
    }

}
