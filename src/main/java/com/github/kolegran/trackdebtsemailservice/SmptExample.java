package com.github.kolegran.trackdebtsemailservice;


import org.apache.commons.mail.*;
import org.apache.commons.mail.HtmlEmail;

import java.io.*;
import javax.activation.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

public class SmptExample {

    public static void main(String[] args) throws Exception {
        HtmlEmail email = new HtmlEmail();

        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("smtp.test.kolegran@gmail.com", "1qazXsw2"));
        email.setSSL(true);

        email.setFrom("smtp.test.kolegran@gmail.com", "Sender");
        email.addTo("user@gmail.com", "Recipient");

        String image = email.embed(resourceImageToDataSource("img.jpg"), "img");

        email.setSubject("TestMail");
        email.setHtmlMsg("<html><body><img src=\"cid:" + image + "\"/></body></html>");
        email.setTextMsg("Contains some message");

        email.send();

        System.out.println("Sending email");
    }

    private static DataSource resourceImageToDataSource(String resourceImageName) throws IOException {
        InputStream is = SmptExample.class.getClassLoader().getResourceAsStream(resourceImageName);
        String contentType = Files.probeContentType(Path.of(resourceImageName));
        return new ByteArrayDataSource(new BufferedInputStream(is).readAllBytes(), contentType);
    }
}
