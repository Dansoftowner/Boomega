package com.dansoftware.libraryapp.update.loader;

import com.dansoftware.libraryapp.update.UpdateInformation;

/**
 * A Loader responsible for loading the information of the new update.
 */
public interface Loader {

    /**
     * Loads the information about the update into an {@link UpdateInformation} object.
     *
     * @return the object that includes all information about the update
     * @throws Exception if some I/O or runtime exception occurs during the operation
     */
    UpdateInformation load() throws Exception;
}
