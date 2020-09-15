package com.dansoftware.libraryapp.locale;

import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Used for internationalizing some embedded javaFX elements if the default {@link Locale} is not supported by
 * the javaFX platform (for example, the default {@link ButtonType}s only supports 10 locale).
 *
 * <p>
 * It uses (even illegal) reflective access operations for get the job done.
 *
 * @author Daniel Gyorffy
 */
final class FXI18N {

    private static final Logger logger = LoggerFactory.getLogger(FXI18N.class);

    /**
     * The path for custom ResourceBundle for internationalizing javaFX
     */
    private static final String BUNDLE_NAME = "com/sun/javafx/scene/control/skin/resources/controls";

    static {
        //list of supported languages in javaFX i18n
        List<Locale> supportedLocales = getSupportedLocales();

        Locale locale = Locale.getDefault();
        if (supportedLocales.contains(locale)) {
            logger.debug("Default locale is supported by javaFX i18n: '{}'", locale);
            logger.debug("No need for internationalizing ButtonTypes");
        } else {
            logger.debug("Default locale is not available in javaFX i18n: '{}'", locale);
            logger.debug("Trying to internationalize ButtonTypes...");
            internationalizeButtonTypes();
        }
    }

    /**
     * Internationalizes the default {@link ButtonType}s through reflection.
     */
    private static void internationalizeButtonTypes() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);

            Class<ButtonType> buttonTypeClass = ButtonType.class;
            Field keyField = modifiableField(buttonTypeClass.getDeclaredField("key"));
            Field textField = modifiableField(buttonTypeClass.getDeclaredField("text"));

            for (ButtonType buttonType : getGlobalButtonTypes()) {
                String key = (String) keyField.get(buttonType);
                String i18nVal = bundle.getString(key);
                textField.set(buttonType, i18nVal);
            }
        } catch (NoSuchFieldException | IllegalAccessException | UnsupportedOperationException e) {
            logger.error("Some error occurred during internationalizing the ButtonTypes", e);
        }
    }

    private static List<ButtonType> getGlobalButtonTypes() {
        return List.of(
                ButtonType.APPLY,
                ButtonType.OK,
                ButtonType.CANCEL,
                ButtonType.CLOSE,
                ButtonType.YES,
                ButtonType.NO,
                ButtonType.FINISH,
                ButtonType.NEXT,
                ButtonType.PREVIOUS
        );
    }

    private static List<Locale> getSupportedLocales() {
        return List.of(
                Locale.ENGLISH,
                Locale.GERMAN,
                Locale.FRENCH,
                Locale.ITALIAN,
                Locale.JAPANESE,
                Locale.KOREAN,
                Locale.CHINESE,
                Locale.SIMPLIFIED_CHINESE,
                new Locale("sv"),
                new Locale("pt", "BR"),
                new Locale("es")
        );
    }

    /**
     * Sets the reflected field accessible and removes the 'final' modifier from it.
     *
     * @param field the {@link Field} to operate on
     * @return the {@link Field} object itself
     */
    @NotNull
    private static Field modifiableField(@NotNull Field field) {
        field.setAccessible(Boolean.TRUE);
        FieldUtils.removeFinalModifier(field);
        return field;
    }

    private FXI18N() {
    }
}
