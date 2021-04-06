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
        try {
            AlertRabbit alertRabbit = new AlertRabbit();
            alertRabbit.setProperties("src/main/resources/rabbit.properties");
            try (Connection connection = alertRabbit.getConnection()) {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("conect", connection);
                JobDetail job = newJob(AlertRabbit.Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(alertRabbit.getProperties().get("rabbit.interval").toString()))
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } catch (InterruptedException | SchedulerException | IOException ex) {
            ex.printStackTrace();
        }
    }
}