package ru.nta.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
  private Properties properties = new Properties();
  private List<Long> store = new ArrayList<>();

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

  public static void main(String[] args) throws IOException {
    try {

      AlertRabbit alertRabbit = new AlertRabbit();
      alertRabbit.setProperties("src/main/resources/rabbit.properties");
      Connection connection = alertRabbit.getConnection();
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      JobDataMap data = new JobDataMap();
      data.put("conect", connection);
      data.put("store", alertRabbit.store);
      JobDetail job = newJob(Rabbit.class)
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
      Thread.sleep(5000);
      scheduler.shutdown();
      System.out.println(alertRabbit.store);
    } catch (SchedulerException se) {
      se.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static class Rabbit implements Job {
    private boolean created;

    public Rabbit() {
      System.out.println(hashCode());
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      System.out.println("Rabbit runs here ...");
      // List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
      Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("conect");
      // store.add(System.currentTimeMillis());
      long timeMillis = System.currentTimeMillis();
      if (created) {
        try (
            PreparedStatement preparedStatement = connection.prepareStatement("Create table rabbit (id serial primary key, created_date);")) {
          preparedStatement.execute();
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        }
        created = true;
      }
      try (
          PreparedStatement preparedStatement = connection.prepareStatement("Insert into rabbit (created_date) values (?);")) {
        preparedStatement.setLong(1, timeMillis);
        preparedStatement.execute();
        System.out.println(connection.prepareStatement("Select * from rabbit").execute());
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }

    }

  }
}