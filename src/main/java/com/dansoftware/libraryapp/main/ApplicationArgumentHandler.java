package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.log.GuiLog;

import static com.dansoftware.libraryapp.util.Bundles.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class responsible for checking the application arguments
 */
public class ApplicationArgumentHandler {

    private static final Logger LOGGER = Logger.getLogger(ApplicationInitializer.class.getName());
    private static File launchedFile;

    ApplicationArgumentHandler(String[] args) {
        if (isEmpty(args))
            return;

        var filePath = args[0];
        var file = new File(filePath);

        if (file.exists()) {
            launchedFile = file;
        } else {
            LOGGER.log(new GuiLog(Level.SEVERE, new FileNotFoundException(filePath), "appargumenthandler.cantopenfile", new Object[]{ file.getName() }));
        }
    }

    private boolean isEmpty(String[] args) {
        return args == null || args.length == 0;
    }

    public static Optional<File> getLaunchedFile() {
        return Optional.ofNullable(launchedFile);
    }

}
