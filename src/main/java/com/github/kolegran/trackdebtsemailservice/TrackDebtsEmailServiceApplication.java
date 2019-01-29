package com.github.kolegran.trackdebtsemailservice;

import org.postgresql.ds.PGSimpleDataSource;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


public class TrackDebtsEmailServiceApplication {

    public static final String DEBT_REMINDER_SERVICE = "debt-reminder-service";

    public static void main(String[] args) throws InterruptedException, SchedulerException {
        Map<String, String> env = System.getenv();
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(env.getOrDefault("DB_HOST", "localhost"));
        dataSource.setPortNumber(Integer.parseInt(env.getOrDefault("DB_PORT", "5434")));
        dataSource.setUser(env.getOrDefault("DB_USERNAME", "postgres"));
        dataSource.setPassword(env.getOrDefault("DB_PASSWORD", "postgres"));
        dataSource.setDatabaseName(env.getOrDefault("DB_DATABASE", "track-debts"));

        UserRowMapper userRowMapper = new UserRowMapper();

        UserRepository userRepository = new UserRepositoryDB(dataSource, userRowMapper);
        NotificationService notificationService = new NotificationService(
                env.getOrDefault("HOSTNAME", "smtp.gmail.com"),
                Integer.parseInt(env.getOrDefault("PORT", "587")),
                env.getOrDefault("USERNAME", "smtp.test.kolegran@gmail.com"),
                env.getOrDefault("PASSWORD", "1qazXsw2"),
                Boolean.parseBoolean(env.getOrDefault("SSL", "true"))
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
