package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.main.VersionInfo;
import com.google.gson.Gson;
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
     * The server-address
     */
    private static final String LOCATION = "https://update-server-ed6c3.firebaseio.com/libraryapp.json";


    private final VersionInfo base;

    /**
     * Creates a basic update searcher object;
     *
     * @param base the base version that the object should compare to; mustn't be null
     * @throws NullPointerException if base is null.
     */
    public UpdateSearcher(@NotNull VersionInfo base) {
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
                VersionInfo newVersion = new VersionInfo(information.getVersion());
                boolean newUpdateAvailable = base.compareTo(newVersion) < 0;
                if (newUpdateAvailable) {
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

        Gson gson = new Gson();
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return gson.fromJson(reader, UpdateInformation.class);
        }
    }

    public static final class UpdateSearchResult {
        private boolean newUpdate;
        private boolean failed;
        private Exception failedCause;
        private UpdateInformation information;

        public boolean newUpdateAvailable() {
            return newUpdate;
        }

        public boolean isFailed() {
            return failed;
        }

        @Nullable
        public Exception getFailedCause() {
            return failedCause;
        }

        @Nullable
        public UpdateInformation getInformation() {
            return information;
        }

        @NotNull
        public UpdateSearchResult ifFailed(@NotNull Consumer<Exception> onFailed) {
            if (failed) {
                onFailed.accept(failedCause);
            }

            return this;
        }

        @NotNull
        public UpdateSearchResult ifNewUpdateAvailable(@NotNull Consumer<UpdateInformation> onAvailable) {
            if (newUpdate) {
                onAvailable.accept(information);
            }

            return this;
        }

        @NotNull
        public UpdateSearchResult ifNoUpdateAvailable(@NotNull Consumer<UpdateInformation> handler) {
            if (!failed && !newUpdate) {
                handler.accept(information);
            }

            return this;
        }
    }
}
