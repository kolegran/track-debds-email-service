package com.github.kolegran.trackdebtsemailservice;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.github.kolegran.trackdebtsemailservice.TrackDebtsEmailServiceApplication.DEBT_REMINDER_SERVICE;

public class ReminderJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            System.out.println("It is going to be sent...");
            DebtReminderService debtReminderService = (DebtReminderService)context.getScheduler().getContext().get(DEBT_REMINDER_SERVICE);
            debtReminderService.sendNotificationsToDebtors(100);
            System.out.println("Sent");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
