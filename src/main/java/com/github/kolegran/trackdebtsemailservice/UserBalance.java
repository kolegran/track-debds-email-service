package com.github.kolegran.trackdebtsemailservice;

import java.math.BigDecimal;

public class UserBalance {
    private User user;
    private BigDecimal amount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
