package ru.nta.quartz;

import org.junit.Assert;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbitTest {

    @Test
    public void checkProperties() throws IOException {
        AlertRabbit alertRabbit = new AlertRabbit();
        alertRabbit.setProperties("src/test/resources/TestRabbit.properties");
        Properties properties = alertRabbit.getProperties();
        Assert.assertEquals(properties.get("rabbit.interval"), "1");
        Assert.assertEquals(properties.get("rabbit.interval2"), "123");
    }

    @Test(expected = FileNotFoundException.class)
    public void failReadProperties() throws IOException {
        AlertRabbit alertRabbit = new AlertRabbit();
        alertRabbit.setProperties("TestRabbit.properties");
    }

    @Test
    public void getConnection() {
        String[] a = new String[0];
        AlertRabbit.main(a);
    }
}