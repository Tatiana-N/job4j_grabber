package ru.nta;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.nta.api.Grab;
import ru.nta.api.Parse;
import ru.nta.api.Store;
import ru.nta.html.SqlRuParse;
import ru.nta.model.Post;
import ru.nta.model.PsqlStore;

import java.io.*;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();
    private final String link;

    public Grabber(String link) {
        this.link = link;
    }


    public Store store() {
        return new PsqlStore(cfg);
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
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
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
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            String link = (String) map.get("link");
            List<Post> list = parse.list(link);
            for (Post post : list) {
                store.save(post);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber("https://www.sql.ru/forum/job-offers/1");
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
    }
}
