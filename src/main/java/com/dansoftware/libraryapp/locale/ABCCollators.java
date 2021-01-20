package com.dansoftware.libraryapp.locale;

import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class ABCCollators {

    private static final Map<Locale, Supplier<Collator>> collators =
            Collections.synchronizedMap(new HashMap<>());

    public static void registerCollator(@NotNull Locale locale, @NotNull Supplier<Collator> collator) {
        collators.put(locale, collator);
    }

    public static Collator getCollator(@NotNull Locale locale) {
        return collators.get(locale).get();
    }

    public static Collator getDefault() {
        return collators.get(Locale.getDefault()).get();
    }

    public static Map<Locale, Supplier<Collator>> getAvailableCollators() {
        return Collections.unmodifiableMap(collators);
    }
}
