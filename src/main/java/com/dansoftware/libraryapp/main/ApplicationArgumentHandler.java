package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.ExceptionUtils;

import java.io.File;
import java.util.Optional;

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
            throw new RuntimeException(ExceptionUtils.getExceptionResourceBundle().getString(""));
        }
    }

    private boolean isEmpty(String[] args) {
        return args.length == 0;
    }

    public static Optional<File> getLaunchedFile() {
        return Optional.ofNullable(launchedFile);
    }

}
