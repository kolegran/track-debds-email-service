package com.github.kolegran.trackdebtsemailservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class UserLedger {
    private Long userId;
    private String fullName;
    private String userEmail;
    private List<UserBalance> userBalanceList = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

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
