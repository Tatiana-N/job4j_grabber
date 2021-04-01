package ru.nta.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
  private Properties properties = new Properties();

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(String str) {
    try {
      properties.load(new FileReader(str));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    try {
     AlertRabbit alertRabbit = new AlertRabbit();
     alertRabbit.setProperties("src/main/resources/rabbit.properties");
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      JobDetail job = newJob(Rabbit.class).build();
      SimpleScheduleBuilder times = simpleSchedule()
          .withIntervalInSeconds(Integer.parseInt(alertRabbit.getProperties().get("rabbit.interval").toString()))
          .repeatForever();
      Trigger trigger = newTrigger()
          .startNow()
          .withSchedule(times)
          .build();
      scheduler.scheduleJob(job, trigger);
    } catch (SchedulerException se) {
      se.printStackTrace();
    }
  }

  public static class Rabbit implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      System.out.println("Rabbit runs here ...");
    }
  }
}