package com.dansoftware.libraryapp.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Deprecated
public class TestLogback {



    static final Logger LOGGER;
    static {
        //LoggerConfigurator.configure();
        LOGGER = LoggerFactory.getLogger(TestLogback.class);
    }

    //@Test
    public void writeInfo() {
        System.out.println(System.getProperty("java.io.tmpdir"));
        LOGGER.info("Logback info!");

        System.out.println(LoggerConfigurator.getLogFilePath());
    }

    //@Test
    public void writeWarning() {
        LOGGER.warn("Logback warn!");
    }

    //@Test
    public void writeError() {
        LOGGER.error("Logback error!");
    }

}