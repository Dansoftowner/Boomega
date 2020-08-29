package com.dansoftware.libraryapp.util;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Provides some time-utilities.
 *
 * @author Daniel Gyorffy
 */
public final class TimeUtils {

    /**
     * A second in milliseconds
     */
    public static final int A_SECOND = 1000;

    /**
     * A minute in milliseconds. {@link #A_SECOND} * 60.
     */
    public static final int A_MINUTE = 60_000;

    /**
     * A hour in milliseconds. {@link #A_MINUTE} * 60.
     */
    public static final int A_HOUR = 3_600_000; // A_MINUTE * 60

    /**
     * A day in milliseconds. {@link #A_HOUR} * 24.
     */
    public static final int A_DAY = 86_400_000; // A_HOUR * 24


    public static final String DAY_KEY = "time.unit.days";
    public static final String HOUR_KEY = "time.unit.hours";
    public static final String MINUTE_KEY = "time.unit.minutes";
    public static final String SECOND_KEY = "time.unit.seconds";
    public static final String MILLI_SECOND_KEY = "time.unit.millis";

    private TimeUtils() {
    }

    /**
     * Returns a human-readable version of the milliseconds.
     *
     * <p>
     * Converts the milliseconds into a larger unit if it's possible.
     * The largest unit is <b>days</b>.
     *
     * <p>
     * It needs a {@link ResourceBundle} to get the internationalized name of the
     * timeUnit.
     * <p>
     * The necessary resource-bundle keys:
     * <pre>{@code
     * "time.unit.days"
     * "time.unit.hours"
     * "time.unit.minutes"
     * "time.unit.seconds"
     * "time.unit.millis"
     * }</pre>
     *
     * @param millis         the milliseconds in {@link Long}
     * @param resourceBundle the {@link ResourceBundle} to read the internationalized time-units from
     * @return the human-readable version of the milliseconds
     */
    public static String countToDisplay(long millis, @NotNull ResourceBundle resourceBundle) {
        String unitName;
        long time;

        if (millis > A_DAY) {
            time = TimeUnit.MILLISECONDS.toDays(millis);
            unitName = resourceBundle.getString(DAY_KEY);
        } else if (millis > A_HOUR) {
            time = TimeUnit.MILLISECONDS.toHours(millis);
            unitName = resourceBundle.getString(HOUR_KEY);
        } else if (millis > A_MINUTE) {
            time = TimeUnit.MILLISECONDS.toMinutes(millis);
            unitName = resourceBundle.getString(MINUTE_KEY);
        } else if (millis > A_SECOND) {
            time = TimeUnit.MILLISECONDS.toSeconds(millis);
            unitName = resourceBundle.getString(SECOND_KEY);
        } else {
            time = millis;
            unitName = resourceBundle.getString(MILLI_SECOND_KEY);
        }

        return String.format("%d %s", time, unitName);
    }
}
