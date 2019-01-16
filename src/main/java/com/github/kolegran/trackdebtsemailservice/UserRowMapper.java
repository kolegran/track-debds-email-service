package com.github.kolegran.trackdebtsemailservice;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper {
    public User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setCardNumber(rs.getString("card_number"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}
