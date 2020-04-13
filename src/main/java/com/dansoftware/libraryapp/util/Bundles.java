package com.dansoftware.libraryapp.util;

import java.util.ResourceBundle;

/**
 * This class is used for accessing Resource Bundles
 *
 * @author Daniel Gyorffy
 */
public class Bundles {

    /**
     * Don't let anyone to create an instance of this class
     */
    private Bundles() {
    }

    public static ResourceBundle getCommonBundle() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.CommonBundle");
    }

    public static ResourceBundle getExceptionBundle() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.ExceptionBundle");
    }

}
