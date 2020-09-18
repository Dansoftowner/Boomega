package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.util.adapter.VersionInteger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An UpdateSearcher used for searching for updates.
 *
 * @author Daniel Gyorffy
 */
public class UpdateSearcher {

    /**
     * The updater server-address
     */
    private static final String LOCATION = "https://update-server-ed6c3.firebaseio.com/libraryapp.json";

    private final VersionInteger base;

    /**
     * Creates a basic update searcher object;
     *
     * @param base the base version that the object should compare to; mustn't be null
     * @throws NullPointerException if base is null.
     */
    public UpdateSearcher(@NotNull VersionInteger base) {
        this.base = Objects.requireNonNull(base, "The base mustn't be null");
    }

    /**
     * Searches for updates; creates an {@link UpdateSearchResult} object that
     * holds all necessary information.
     *
     * @return the result of the search
     */
    @NotNull
    public UpdateSearchResult search() {
        UpdateSearchResult result = new UpdateSearchResult();
        try {
            var information = loadInfo();
            if (information != null) {
                VersionInteger newVersion = new VersionInteger(information.getVersion());
                if (base.isOlderThan(newVersion)) {
                    result.newUpdate = true;
                    result.information = information;
                }
            }
        } catch (IOException | RuntimeException e) {
            result.failed = true;
            result.failedCause = e;
        }

        return result;
    }

    /**
     * Loads the update-information from the server into a {@link UpdateInformation} object.
     *
     * @return the {@link UpdateInformation} object.
     * @throws IOException if some IO exception occurs
     */
    private UpdateInformation loadInfo() throws IOException {
        URL url = new URL(LOCATION);
        URLConnection connection = url.openConnection();
        connection.connect();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(UpdateInformation.class, new UpdateInformationDeserializer())
                .create();

        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return gson.fromJson(reader, UpdateInformation.class);
        }
    }

    /**
     * An UpdateSearchResult object can hold all information
     * about an update-search.
     *
     * @see UpdateSearcher#search()
     */
    public static final class UpdateSearchResult {
        private boolean newUpdate;
        private boolean failed;
        private Exception failedCause;
        private UpdateInformation information;

        /**
         * This method can tell us that a new update is available or not.
         * If it is, that means that we can use the {@link #getInformation()}
         * method to get the concrete information about the new update.
         *
         * @return {@code true} if new update is available; {@code false} otherwise.
         */
        public boolean newUpdateAvailable() {
            return newUpdate;
        }

        /**
         * This method can tell us that the update-searching was failed.
         * If it failed, we can use the {@link #getFailedCause()} method
         * to get the concrete {@link Exception} that caused the failure.
         *
         * @return {@code true} if the update-searching was failed; {@code false} otherwise.
         */
        public boolean isFailed() {
            return failed;
        }

        /**
         * @see #isFailed()
         */
        @Nullable
        public Exception getFailedCause() {
            return failedCause;
        }

        /**
         * @see #newUpdateAvailable()
         */
        @Nullable
        public UpdateInformation getInformation() {
            return information;
        }

        /**
         * This:
         * <pre>{@code
         * UpdateSearcher.UpdateSearchResult result = ...;
         * if (result.isFailed()) {
         *     Exception cause = result.getFailedCause();
         *     //handle
         * }
         * }</pre>
         * ... can be simplified by this method to this:
         * <pre>{@code
         * result.ifFailed(cause -> {
         *    //handle
         * });
         * }</pre>
         *
         * @param onFailed the {@link Consumer} that defines what to do; it mustn't be null
         * @see #isFailed()
         * @see #getFailedCause()
         */
        @NotNull
        public UpdateSearchResult ifFailed(@NotNull Consumer<Exception> onFailed) {
            if (failed)
                onFailed.accept(failedCause);
            return this;
        }

        /**
         * This:
         * <pre>{@code
         * UpdateSearcher.UpdateSearchResult result = ...;
         * if (result.newUpdateAvailable()) {
         *    UpdateInformation updateInformation = result.getInformation();
         *    //handle
         * }
         * }</pre>
         * ... can be simplified to this:
         * <pre>{@code
         * result.ifNewUpdateAvailable(updateInformation -> {
         *    //handle
         * });
         * }</pre>
         *
         * @param onAvailable the {@link Consumer} that defines what to do; mustn't be null
         * @see #newUpdateAvailable()
         * @see #getInformation()
         */
        @NotNull
        public UpdateSearchResult ifNewUpdateAvailable(@NotNull Consumer<UpdateInformation> onAvailable) {
            if (newUpdate)
                onAvailable.accept(information);
            return this;
        }

        /**
         * This:
         * <pre>{@code
         * UpdateSearcher.UpdateSearchResult result = ...;
         * if (!result.isFailed() && !result.newUpdateAvailable()) {
         *    UpdateInformation updateInformation = result.getInformation();
         *    //handle
         * }
         * }</pre>
         * ... can be simplified to this:
         * <pre>{@code
         * result.ifNoUpdateAvailable(updateInformation -> {
         *     //handle
         * });
         * }</pre>
         *
         * @param handler the {@link Consumer} that defines what to do; mustn't be null
         * @see #isFailed()
         * @see #newUpdateAvailable()
         * @see #getInformation()
         */
        @NotNull
        public UpdateSearchResult ifNoUpdateAvailable(@NotNull Consumer<UpdateInformation> handler) {
            if (!failed && !newUpdate)
                handler.accept(information);
            return this;
        }
    }
}
