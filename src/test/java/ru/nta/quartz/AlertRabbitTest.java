package ru.nta.quartz;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class AlertRabbitTest {

  @Test
  public void checkProperties(){
    AlertRabbit alertRabbit = new AlertRabbit();
    alertRabbit.setProperties("src/test/resources/TestRabbit.properties");
    Properties properties = alertRabbit.getProperties();
    Assert.assertEquals(properties.get("rabbit.interval"),"1");
    Assert.assertEquals(properties.get("rabbit.interval2"),"123");
  }
}