package ru.nta;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.nta.api.Grab;
import ru.nta.api.Parse;
import ru.nta.api.DaoStore;
import ru.nta.html.SqlRuParse;
import ru.nta.model.Post;
import ru.nta.dao.PsqlStore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab<Post> {
    private final Properties cfg = new Properties();
    private final String link;

    public Grabber(String link) {
        this.link = link;
    }


    public DaoStore<Post> store(String tableName) {
        return new PsqlStore(cfg, tableName);
    }

    public void web(DaoStore<Post> daoStore) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : daoStore.getAll()) {
                            out.write(post.toString().getBytes("windows-1251"));
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in = new FileInputStream("src/main/resources/rabbit.properties")) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse<Post> parse, DaoStore<Post> daoStore, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", daoStore);
        data.put("parse", parse);
        data.put("link", link);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            DaoStore<Post> daoStore = (DaoStore<Post>) map.get("store");
            Parse<Post> parse = (Parse<Post>) map.get("parse");
            String link = (String) map.get("link");
            List<Post> list = parse.list(link);
            for (Post post : list) {
                daoStore.save(post);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber("https://www.sql.ru/forum/job-offers/1");
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        DaoStore<Post> store = grab.store("post");
        grab.init(new SqlRuParse(), store, scheduler);
        grab.web(store);
    }
}
