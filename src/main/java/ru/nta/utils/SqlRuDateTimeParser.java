package ru.nta.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SqlRuDateTimeParser implements DateTimeParser {
    String time;

    List<String> shortMonths = Arrays.asList(
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек");
    LocalDateTime dateTime;

    @Override
    public LocalDateTime parse(String parse) {
        dateTime = LocalDateTime.now();
        parse = parse.replaceAll(",", "").trim();
        if (parse.startsWith("вчера")) {
            dateTime = dateTime.minusDays(1);
            time = parse.substring(parse.indexOf(" ") + 1);
        } else if (parse.startsWith("сегодня")) {
            time = parse.substring(parse.indexOf(" ") + 1);
        } else {
            String[] dataNumber = parse.split(" ");
            dateTime = dateTime.withDayOfMonth(1);
            dateTime = dateTime.withMonth(shortMonths.indexOf(dataNumber[1]) + 1);
            dateTime = dateTime.withDayOfMonth(Integer.parseInt(dataNumber[0]));
            dateTime = dateTime.withYear(Integer.parseInt(20 + dataNumber[2]));
            time = dataNumber[3];
        }
        dateTime = dateTime.withHour(getHours(time));
        dateTime = dateTime.withMinute(getMinute(time));
        dateTime = dateTime.withSecond(0);
        dateTime = dateTime.withNano(0);
        return dateTime;
    }

    private int getHours(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[0]);
    }

    private int getMinute(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[1]);
    }
}