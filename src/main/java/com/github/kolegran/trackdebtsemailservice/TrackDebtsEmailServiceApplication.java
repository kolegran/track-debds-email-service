package com.github.kolegran.trackdebtsemailservice;

import org.postgresql.ds.PGSimpleDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class TrackDebtsEmailServiceApplication {

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(5434);
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabaseName("track-debts");
        Connection connection = dataSource.getConnection();

        List<UserLedger> userLedgers = getUserLedgers(connection);

        Set<UserLedger> victim = new HashSet<>();

        for (UserLedger userLedger : userLedgers) {
            for (UserBalance userBalance : userLedger.userBalanceList) {
                if (userBalance.amount.compareTo(new BigDecimal(-80)) < 0) {
                    victim.add(userLedger);
                }
            }
        }

        for (UserLedger userLedger : victim) {
            sendEmail(userLedger);

        }
        connection.close();
    }

    private static void sendEmail(UserLedger userLedger) {
        try {
            SmptExample.emailSender(userLedger.description());
        } catch (Exception e)
        {
            System.err.println("Error");
        }
    }

    private static List<UserLedger> getUserLedgers(Connection connection) throws SQLException {
        ArrayList<UserLedger> res = new ArrayList<>();

        ResultSet userRs = connection.createStatement().executeQuery("select * from users order by id");

        while (userRs.next()) {
            UserLedger userLedger = new UserLedger();
            userLedger.userId = userRs.getLong("id");
            userLedger.fullName = userRs.getString("first_name") + " " + userRs.getString("last_name");
            Map<Long, BigDecimal> sumPerUser = getLedger(connection, userRs.getLong("id"));

            for (Long userId : sumPerUser.keySet()) {
                UserBalance userBalance = new UserBalance();
                userBalance.userId = userId;
                userBalance.fullName = getFullNameByUserId(connection, userId);
                userBalance.amount = sumPerUser.get(userId);
                userLedger.userBalanceList.add(userBalance);
            }

            res.add(userLedger);
        }
        return res;
    }

    private static String getFullNameByUserId(Connection connection, Long userId)  throws SQLException {
        ResultSet result = connection.createStatement().executeQuery("select * from users where id = " + userId + "order by id");

        result.next();

        return result.getString("first_name") + " " + result.getString("last_name");
    }

    private static Map<Long, BigDecimal> getLedger(Connection connection, long userId) throws SQLException {
        ResultSet senderRs = connection.createStatement().executeQuery(
                "select * from money_transaction where sender_id = " + userId + " or receiver_id = " + userId
        );

        Map<Long, BigDecimal> sumPerUser = new HashMap<>();

        while (senderRs.next()) {
            long receiverId = senderRs.getLong("receiver_id");
            long senderId = senderRs.getLong("sender_id");

            if (userId == senderId) {
                BigDecimal sum = sumPerUser.getOrDefault(receiverId, BigDecimal.ZERO);
                sumPerUser.put(receiverId, sum.add(senderRs.getBigDecimal("amount")));
            } else {
                BigDecimal sum = sumPerUser.getOrDefault(senderId, BigDecimal.ZERO);
                sumPerUser.put(senderId, sum.subtract(senderRs.getBigDecimal("amount")));
            }
        }

        return sumPerUser;
    }

}

class UserLedger {
    Long userId;
    String fullName;
    List<UserBalance> userBalanceList = new ArrayList<>();

    BigDecimal totalDebt() {
        BigDecimal s = BigDecimal.ZERO;
        for (UserBalance userBalance : userBalanceList) {
            if (userBalance.amount.compareTo(BigDecimal.ZERO) < 0) {
                s = s.add(userBalance.amount);
            }
        }
        return s.multiply(new BigDecimal(-1));
    }

    public String description() {
        /*System.out.println("User id is: " + userId);
        System.out.println("User full name: " + fullName);

        for (UserBalance u : userBalanceList) {
            System.out.println("You are the debtor of " + u.fullName + ". The debt is: " + u.amount);
        }

        System.out.println("Total debt is: " + totalDebt());
        System.out.println();*/

        // for test
        return userId.toString();
    }
}

class UserBalance {
    Long userId;
    String fullName;
    BigDecimal amount;
}