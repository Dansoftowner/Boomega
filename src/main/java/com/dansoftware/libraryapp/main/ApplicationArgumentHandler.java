package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.log.GuiLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class responsible for checking the application arguments
 */
public class ApplicationArgumentHandler {

    private static final Logger LOGGER = Logger.getLogger(ApplicationInitializer.class.getName());

    /**
     * This field contains the launched file
     */
    private static File launchedFile;

    /**
     * This constructor should be called with the application arguments
     * When this constructor called with the array, it does automatically the
     * work so there is no need to invoke other method(s) to parse the application
     * arguments
     *
     * @param args the application arguments
     * @see Main#main(String[])
     */
    ApplicationArgumentHandler(String[] args) {
        if (isEmpty(args))
            return;

        String filePath = args[0];
        File file = new File(filePath);

        if (file.exists()) {
            launchedFile = file;
        } else {
            LOGGER.log(new GuiLog(Level.SEVERE, new FileNotFoundException(filePath), "appargumenthandler.cantopenfile", new Object[]{ file.getName() }));
        }
    }

    /**
     * Returns that the array is empty
     *
     * @param args the array
     * @return <code>true</code> - if the array is empty <code>false</code> - otherwise
     */
    private boolean isEmpty(String[] args) {
        return args == null || args.length == 0;
    }

    /**
     * Returns the launched file with an Optional wrapper
     *
     * @return the launched file with Optional
     */
    public static Optional<File> getLaunchedFile() {
        return Optional.ofNullable(launchedFile);
    }

}
