package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Used for transforming the application arguments into it's java Object representation ({@link DatabaseMeta}).
 *
 * <p>
 * We only care about the first application argument.
 *
 * @author Daniel Gyorffy
 */
public class ArgumentTransformer {

    private ArgumentTransformer() {
    }

    @Nullable
    public static DatabaseMeta transform(String arg) {
        return transformOptional(arg).orElse(null);
    }

    @Nullable
    public static DatabaseMeta transform(List<String> args) {
        return transformOptional(args).orElse(null);
    }

    public static Optional<DatabaseMeta> transformOptional(@NotNull String arg) {
        return Optional.of(new DatabaseMeta(new File(arg)));
    }

    public static Optional<DatabaseMeta> transformOptional(@Nullable List<String> args) {
        if (CollectionUtils.isEmpty(args)) {
            return Optional.empty();
        }

        return transformOptional(args.get(0));
    }

}
