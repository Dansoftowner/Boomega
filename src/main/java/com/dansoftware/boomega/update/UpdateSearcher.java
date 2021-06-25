package com.dansoftware.boomega.update;

import io.github.g00fy2.versioncompare.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Abstraction for an object that can search for updates in a particular way.
 *
 * <p>
 * Also, this class should be used when we want to create an {@link UpdateSearcher} object.
 * We use the static {@link #newInstance(Version)} method for creating the particular instance,
 * because it provides testability.
 * <p>
 * Example:
 * <pre>{@code
 * UpdateSearcher updateSearcher = UpdateSearcher.newInstance(...);
 * //...using it
 * }</pre>
 *
 * @author Daniel Gyorffy
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class UpdateSearcher {

    private static final Function<@NotNull Version, @NotNull UpdateSearcher> DEFAULT_INSTANCE_FACTORY;
    private static Function<@NotNull Version, @NotNull UpdateSearcher> instanceFactory;

    static {
        DEFAULT_INSTANCE_FACTORY = DefaultUpdateSearcher::new;
        instanceFactory = DEFAULT_INSTANCE_FACTORY;
    }

    /**
     * Searches for updates; creates an {@link UpdateSearchResult} object that
     * holds all necessary information.
     *
     * @return the result of the search
     */
    @NotNull
    public abstract UpdateSearchResult search();

    /**
     * Loads the update-information from the server into a {@link UpdateInformation} object.
     *
     * @return the {@link UpdateInformation} object.
     * @throws IOException if some IO exception occurs
     */
    @Nullable
    protected abstract UpdateInformation loadInfo() throws IOException;

    /**
     * Creates a particular {@link UpdateSearcher} object.
     *
     * @param base the base version that the object should compare to; mustn't be null
     * @return the object instance
     */
    public static UpdateSearcher newInstance(@NotNull Version base) {
        return instanceFactory.apply(base);
    }

    public static UpdateSearcher defaultInstance() {
        return instanceFactory.apply(new Version(System.getProperty("boomega.version")));
    }

    static void setInstanceFactory(@NotNull Function<@NotNull Version, @NotNull UpdateSearcher> instanceFactory) {
        Objects.requireNonNull(instanceFactory);
        UpdateSearcher.instanceFactory = instanceFactory;
    }

    /**
     * An UpdateSearchResult object can hold all information
     * about an update-search.
     *
     * @see DefaultUpdateSearcher#search()
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

        void setNewUpdate(boolean newUpdate) {
            this.newUpdate = newUpdate;
        }

        void setFailed(boolean failed) {
            this.failed = failed;
        }

        void setFailedCause(Exception failedCause) {
            this.failedCause = failedCause;
        }

        void setInformation(UpdateInformation information) {
            this.information = information;
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
        public DefaultUpdateSearcher.UpdateSearchResult ifFailed(@NotNull Consumer<Exception> onFailed) {
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
        public DefaultUpdateSearcher.UpdateSearchResult ifNewUpdateAvailable(@NotNull Consumer<UpdateInformation> onAvailable) {
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
        public DefaultUpdateSearcher.UpdateSearchResult ifNoUpdateAvailable(@NotNull Consumer<UpdateInformation> handler) {
            if (!failed && !newUpdate)
                handler.accept(information);
            return this;
        }
    }
}
