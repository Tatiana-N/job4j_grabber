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


public class AlertRabbit {
    private final Properties properties = new Properties();
    private final List<Long> store = new ArrayList<>();

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
        private boolean created;

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
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
}