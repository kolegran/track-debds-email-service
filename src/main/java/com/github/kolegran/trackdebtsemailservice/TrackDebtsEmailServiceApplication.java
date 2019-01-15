package com.github.kolegran.trackdebtsemailservice;

import org.postgresql.ds.PGSimpleDataSource;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


public class TrackDebtsEmailServiceApplication {

    public static final String DEBT_REMINDER_SERVICE = "debt-reminder-service";

    public static void main(String[] args) throws InterruptedException, SchedulerException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5434);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("track-debts");

        UserRepository userRepository = new UserRepositoryDB(dataSource);
        NotificationService notificationService = new NotificationService();

        DebtReminderService debtReminderService = new DebtReminderService(userRepository, notificationService);

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.getContext().put(DEBT_REMINDER_SERVICE, debtReminderService);

        JobDetail job = newJob(ReminderJob.class)
                .withIdentity("reminder-job", "td-group")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("daily-based-for-reminder", "td-group")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * * * ?"))
                .build();

        scheduler.scheduleJob(job, trigger);

        scheduler.start();
    }
}
