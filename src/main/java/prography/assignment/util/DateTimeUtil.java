package prography.assignment.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {

    private static final String DEFAULT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static String formatDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME));
    }
}
