package ru.nta.api;


import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}