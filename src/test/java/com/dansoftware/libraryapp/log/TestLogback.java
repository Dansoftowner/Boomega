package com.dansoftware.libraryapp.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Deprecated
public class TestLogback {


    static final Logger LOGGER;
    static {
        System.setProperty("logfile.path", ("C:\\Users\\judal\\Music\\LOOOG\\LOG_MOGI.log"));
        LOGGER = LoggerFactory.getLogger(TestLogback.class);
    }

    @Test
    public void writeInfo() {

        LOGGER.info("Logback info!");
    }

    @Test
    public void writeWarning() {
        LOGGER.warn("Logback warn!");
    }

    @Test
    public void writeError() {
        LOGGER.error("Logback error!");
    }

}
