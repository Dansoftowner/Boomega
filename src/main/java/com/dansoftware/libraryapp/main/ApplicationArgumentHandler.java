package com.dansoftware.libraryapp.main;

import static com.dansoftware.libraryapp.util.Bundles.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * This class responsible for checking the application arguments
 */
public class ApplicationArgumentHandler {

    private static File launchedFile;

    ApplicationArgumentHandler(String[] args) {
        if (isEmpty(args))
            return;

        var filePath = args[0];
        var file = new File(filePath);

        if (file.exists()) {
            launchedFile = file;
        } else {
            throw new RuntimeException(getExceptionBundle().getString("appargumenthandler.cantopenfile") + " : " + filePath, new FileNotFoundException(filePath));
        }
    }

    private boolean isEmpty(String[] args) {
        return args.length == 0;
    }

    public static Optional<File> getLaunchedFile() {
        return Optional.ofNullable(launchedFile);
    }

}
