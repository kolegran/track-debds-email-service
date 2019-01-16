package com.github.kolegran.trackdebtsemailservice;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                userLedger.setFullName(userRs.getString("first_name") + " " + userRs.getString("last_name"));
                userLedger.setUserEmail(userRs.getString("email"));
                Map<Long, BigDecimal> sumPerUser = getLedger(userRs.getLong("id"));

                for (Long userId : sumPerUser.keySet()) {
                    UserBalance userBalance = new UserBalance();
                    userBalance.setUserId(userId);
                    userBalance.setFullName(getFullNameByUserId(userId));
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

    public String getFullNameByUserId(Long userId)  throws SQLException {
        ResultSet result = dataSource.getConnection().createStatement().executeQuery("select * from users where id = " + userId + "order by id");

        result.next();


        String firstName = result.getString("first_name");
        String lastName = result.getString("last_name");

        return (firstName == null || lastName == null) ? null : (firstName + lastName);
    }
}
