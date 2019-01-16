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

        UserRowMapper userRowMapper = new UserRowMapper();

        UserRepository userRepository = new UserRepositoryDB(dataSource, userRowMapper);
        NotificationService notificationService = new NotificationService(
                "smtp.gmail.com",
                587,
                "smtp.test.kolegran@gmail.com",
                "1qazXsw2",
                true
        );

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
