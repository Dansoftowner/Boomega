package com.dansoftware.libraryapp.util;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.Objects;

/**
 * This class contains some utility method
 * for handling files.
 *
 * @author Daniel Gyorffy
 */
public class FileUtils {

    /**
     * Don't let anyone to create an instance of this class.
     */
    private FileUtils() {
    }

    /**
     * Checks that the path of the file is valid or not.
     * If the file object is null it returns immediately false;
     *
     * Example (on Windows):
     *
     * <pre>
     *     "C:/Users/User/test.txt" -> {@code true}
     *     "C?,:.f" -> {@code false}s
     * </pre>
     *
     * @param file the file that we want to check; may be null
     * @return {@code true} if the filepath is valid; {@code false} otherwise.
     */
    public static boolean hasValidPath(File file) {
        try {
            file.toPath();
            return true;
        } catch (InvalidPathException | NullPointerException e) {
            return false;
        }
    }

    public static boolean hasNotValidPath(File file) {
        return !hasValidPath(file);
    }

    public static String shortenedFilePath(File file, int maxBack) {
        if (Objects.isNull(file) || maxBack < 0) {
            return "";
        }

        StringBuilder shortenedPath = new StringBuilder(file.getName());

        File lastParent = file.getParentFile();
        while(maxBack > 0) {
            if (Objects.nonNull(lastParent)) {
                shortenedPath.insert(0, lastParent.getName() + File.separator);
                lastParent = lastParent.getParentFile();
            } else break;

            maxBack--;
        }

        if (Objects.nonNull(lastParent)) {
            shortenedPath.insert(0, "..." + File.separator);
        }

        return shortenedPath.toString();
    }

    /**
     * Creates a file (or a directory) if it's not exists yet.
     *
     *
     * @param file      the object that represents the file that we want to create
     * @param directory a boolean value that represents what kind of 'file' that we want to create.
     *                  True if we want to create a directory, false if we want to create a regular file.
     * @return the file that created <i>basically the same file object that passed as parameter</i>
     * @throws UnableToCreateFileException if the file cannot be created
     */
    public static File createFile(File file, boolean directory) {
        if (!file.exists()) {
            if (directory) {
                if (!file.mkdirs()) {
                    throw new UnableToCreateFileException("Directory cannot be created: " + file.getAbsolutePath());
                }
            } else {
                try {
                    if (!file.createNewFile()) throw new UnableToCreateFileException("File cannot be created: " + file.getAbsolutePath());
                } catch (IOException ex) {
                    throw new UnableToCreateFileException(ex);
                }
            }
        }

        return file;
    }


    /**
     * This method renames the given file this way:
     * <ul>
     *     <li>Generates a random 5-digit number</li>
     *     <li>Adds the '_old' word to the original name of the file</li>
     *     <li>Adds the random number to the end of the file name</li>
     * </ul>
     * <i>If a file already exists with the generated file name, the process
     * will be repeated again</i>
     *
     * <p>
     * So we don't delete completely the old file,
     * we just rename it and then the program can create a new file with the
     * original name
     *
     * @param file the file that we want to rename
     */
    public static void makeOldFileOf(File file) {

        File directoryOfFile = file.getParentFile();
        String nameOfFile = file.getName();

        File generated;
        do {
            int random = (int) (Math.random() * Math.pow(10, 5));
            generated = new File(directoryOfFile, String.format("%s_%s%d", nameOfFile, "old", random));
        } while (generated.exists());

        if (!file.renameTo(generated)) {
            throw new UnableToCreateFileException("The file : '" + file.getAbsolutePath() + "' cannot be renamed to: '" + generated + "'");
        }
    }

    public static final class UnableToCreateFileException extends RuntimeException {
        public UnableToCreateFileException() {
        }

        public UnableToCreateFileException(String message) {
            super(message);
        }

        public UnableToCreateFileException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnableToCreateFileException(Throwable cause) {
            super(cause);
        }

        public UnableToCreateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
