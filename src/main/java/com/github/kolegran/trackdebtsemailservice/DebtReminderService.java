package com.github.kolegran.trackdebtsemailservice;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebtReminderService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DebtReminderService(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void sendNotificationsToDebtors(int threshold) {
        List<UserLedger> userLedgers = userRepository.getUserLedgers();

        Set<UserLedger> victim = new HashSet<>();

        for (UserLedger userLedger : userLedgers) {
            for (UserBalance userBalance : userLedger.getUserBalanceList()) {
                if (userBalance.getAmount().compareTo(new BigDecimal(-1 * threshold)) < 0) {
                    victim.add(userLedger);
                }
            }
        }

        for (UserLedger userLedger : victim) {
            notificationService.send(userLedger);
        }
    }
}
