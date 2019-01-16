package com.github.kolegran.trackdebtsemailservice;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserRepositoryDB implements UserRepository {
    private final DataSource dataSource;

    public UserRepositoryDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<UserLedger> getUserLedgers() {
        try {
            ArrayList<UserLedger> res = new ArrayList<>();

            ResultSet userRs = dataSource.getConnection().createStatement().executeQuery("select * from users order by id");

            while (userRs.next()) {
                UserLedger userLedger = new UserLedger();
                userLedger.setUserId(userRs.getLong("id"));
                userLedger.setFullName(getDisplayName(userRs.getString("first_name"), userRs.getString("last_name"), userRs.getString("email")));
                userLedger.setUserEmail(userRs.getString("email"));
                Map<Long, BigDecimal> sumPerUser = getLedger(userRs.getLong("id"));

                for (Long userId : sumPerUser.keySet()) {
                    UserBalance userBalance = new UserBalance();
                    userBalance.setUserId(userId);

                    ResultSet fullNames = getFullNameByUserId(userId);
                    userBalance.setFullName(getDisplayName(fullNames.getString("first_name"), fullNames.getString("last_name"), fullNames.getString("email")));

                    userBalance.setAmount(sumPerUser.get(userId));
                    userLedger.getUserBalanceList().add(userBalance);
                }

                res.add(userLedger);
            }
            return res;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<Long, BigDecimal> getLedger(long userId) throws SQLException {
        ResultSet senderRs = dataSource.getConnection().createStatement().executeQuery(
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

    public ResultSet getFullNameByUserId(Long userId)  throws SQLException {
        ResultSet result = dataSource.getConnection().createStatement().executeQuery("select * from users where id = " + userId + "order by id");

        result.next();

        return result;
    }

    public String getDisplayName(String firstName, String lastName, String userEmail) {
        return Optional.ofNullable(getFullName(firstName, lastName)).orElse(userEmail);
    }

    public String getFullName(String firstName, String lastName) {
        if (firstName == null) {
            return Optional.ofNullable(lastName).orElse(null);
        }
        return firstName + (lastName == null ? "" : (" " + lastName));
    }
}
