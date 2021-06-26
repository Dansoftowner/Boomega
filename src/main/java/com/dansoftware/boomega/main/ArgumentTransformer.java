/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.main;

import com.dansoftware.boomega.db.DatabaseMeta;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
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
        if (StringUtils.isBlank(arg))
            return Optional.empty();

        return Optional.ofNullable(DatabaseMeta.parseFrom(arg));
    }

    public static Optional<DatabaseMeta> transformOptional(@Nullable List<String> args) {
        if (CollectionUtils.isEmpty(args)) {
            return Optional.empty();
        }

        return transformOptional(args.get(0));
    }

}
