package com.github.kolegran.trackdebtsemailservice;

import java.util.List;

public interface UserRepository {
    List<UserLedger> getUserLedgers();
}
