package ru.nta.api;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface Grab<T> {
    void init(Parse<T> parse, DaoStore<T> daoStore, Scheduler scheduler) throws SchedulerException;
}