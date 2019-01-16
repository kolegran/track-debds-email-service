package com.github.kolegran.trackdebtsemailservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserLedger {
    private User user;
    private List<UserBalance> userBalanceList = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserBalance> getUserBalanceList() {
        return userBalanceList;
    }

    public void setUserBalanceList(List<UserBalance> userBalanceList) {
        this.userBalanceList = userBalanceList;
    }

    public BigDecimal totalDebt() {
        BigDecimal s = BigDecimal.ZERO;
        for (UserBalance userBalance : userBalanceList) {
            if (userBalance.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                s = s.add(userBalance.getAmount());
            }
        }
        return s.multiply(new BigDecimal(-1));
    }
}
