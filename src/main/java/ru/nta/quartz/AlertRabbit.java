package ru.nta.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class AlertRabbit {
    private final Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(String str) throws IOException {
        properties.load(new FileReader(str));
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(properties.getProperty("jdbc.driver"));
            connection = DriverManager.getConnection(
                    properties.getProperty("jdbc.url"),
                    properties.getProperty("jdbc.username"),
                    properties.getProperty("jdbc.password"));
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) { //throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("conect");
            long timeMillis = System.currentTimeMillis();
            try (
                    PreparedStatement preparedStatement = connection.prepareStatement("Insert into rabbit (created_date,hashCode) values (?,?);")) {
                preparedStatement.setLong(1, timeMillis);
                preparedStatement.setLong(2, this.hashCode());
                preparedStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
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