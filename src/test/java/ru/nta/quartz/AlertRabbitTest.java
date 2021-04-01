package ru.nta.quartz;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AlertRabbitTest {

  @Test
  public void checkProperties() throws IOException {
    AlertRabbit alertRabbit = new AlertRabbit();
    alertRabbit.setProperties("src/test/resources/TestRabbit.properties");
    Properties properties = alertRabbit.getProperties();
    Assert.assertEquals(properties.get("rabbit.interval"),"1");
    Assert.assertEquals(properties.get("rabbit.interval2"),"123");
  }
  @Test(expected = FileNotFoundException.class)
  public void failReadProperties() throws IOException {
    AlertRabbit alertRabbit = new AlertRabbit();
    alertRabbit.setProperties("TestRabbit.properties");
  }
}