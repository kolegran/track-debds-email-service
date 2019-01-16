package com.github.kolegran.trackdebtsemailservice;

import java.util.Optional;

public class User {
    private Long id;
    private String cardNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        if (firstName == null) {
            return Optional.ofNullable(lastName).orElse(null);
        }
        return firstName + (lastName == null ? "" : (" " + lastName));
    }

    public String getDisplayName() {
        return Optional.ofNullable(getFullName()).orElse(email);
    }
}
