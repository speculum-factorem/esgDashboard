package com.esg.dashboard.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class TimeRangeUtils {

    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS)
                .minusDays(dateTime.getDayOfWeek().getValue() - 1);
    }

    public static LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS)
                .withDayOfMonth(1);
    }

    public static boolean isWithinLastDays(LocalDateTime dateTime, int days) {
        return dateTime.isAfter(LocalDateTime.now().minusDays(days));
    }

    public static String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return (milliseconds / 1000) + "s";
        } else {
            return (milliseconds / 60000) + "m " + ((milliseconds % 60000) / 1000) + "s";
        }
    }
}