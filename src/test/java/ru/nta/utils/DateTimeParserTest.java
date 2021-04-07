package ru.nta.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDateTime;

public class DateTimeParserTest {

    @Test
    public void parse() throws ParseException {
        DateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        LocalDateTime time = sqlRuDateTimeParser.parse("вчера, 19:23");
        LocalDateTime time1 = sqlRuDateTimeParser.parse("2 дек 19, 22:29");
        LocalDateTime time2 = sqlRuDateTimeParser.parse("25 июн 20, 21:56");
        LocalDateTime time3 = sqlRuDateTimeParser.parse("сегодня, 01:11");
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        Assert.assertEquals(time, LocalDateTime.now().minusDays(1).withHour(19).withMinute(23).withSecond(0).withNano(0));
        Assert.assertEquals(time1, LocalDateTime.of(2019, 12, 2, 22, 29, 0));
        Assert.assertEquals(time2, LocalDateTime.of(2020, 6, 25, 21, 56, 0));
        Assert.assertEquals(time3, LocalDateTime.now().withHour(1).withMinute(11).withSecond(0).withNano(0));
    }
}