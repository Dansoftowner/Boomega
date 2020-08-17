package com.dansoftware.libraryapp.main;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class InstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);

    private static final String APPLICATION_ID = "com.dansoftware.libraryapp";

    private static final String FOCUS = "focus";

    private static InstanceService instance;

    private InstanceService(String[] args) {
        try {
            JUnique.acquireLock(APPLICATION_ID, arg -> {
                if (FOCUS.equals(arg)) {
                    //focusing on some window
                } else {
                    //if the file that the argument represents is already opened
                    //with the application, we focus on that window, otherwise, we just
                    //open the AppEntry with it
                }
                return null;
            });
        } catch (AlreadyLockedException e) {
            logger.info("An application is already running with the id: '" + APPLICATION_ID + "'");
            logger.info("Sending the arguments to the already running instance...");

            if (ArrayUtils.isEmpty(args)) {
                JUnique.sendMessage(APPLICATION_ID, FOCUS);
            } else {
                JUnique.sendMessage(APPLICATION_ID, args[0]);
            }

            logger.info("Exiting...");
            System.exit(1);
        }
    }


    public static void open(String[] args) {
        if (instance == null) {
            synchronized (InstanceService.class) {
                if (instance == null) {
                    instance = new InstanceService(args);
                }
            }
        }
    }

}
