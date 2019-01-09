package com.github.kolegran.trackdebtsemailservice;

import org.apache.commons.mail.*;
import org.apache.commons.mail.HtmlEmail;

public class SmptExample {

    public static void main(String[] args) throws Exception {
        // TODO
    }

    public static void emailSender(String info) throws Exception {
        HtmlEmail email = new HtmlEmail();

        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("smtp.test.kolegran@gmail.com", "1qazXsw2"));
        email.setSSL(true);

        email.setFrom("smtp.test.kolegran@gmail.com", "Sender");
        email.addTo("kolegran@gmail.com", "Recipient");

        email.setSubject("TestMail");
        email.setHtmlMsg("<html><body></body></html>");
        email.setTextMsg("Contains some message");

        email.send();

        System.out.println("Sending email");
    }
}
