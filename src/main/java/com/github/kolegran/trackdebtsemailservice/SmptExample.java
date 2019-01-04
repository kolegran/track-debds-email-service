package com.github.kolegran.trackdebtsemailservice;

import org.apache.commons.mail.*;

public class SmptExample {

    public static void main(String[] args) throws Exception {
        Email email = new SimpleEmail();

        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("smtp.test.kolegran@gmail.com", "1qazXsw2"));
        email.setSSL(true);

        email.setFrom("smtp.test.kolegran@gmail.com");
        email.setSubject("TestMail");
        email.setMsg("Contains some text");
        email.addTo("user@gmail.com");

        email.send();

        System.out.println("Sending email");
    }
}
