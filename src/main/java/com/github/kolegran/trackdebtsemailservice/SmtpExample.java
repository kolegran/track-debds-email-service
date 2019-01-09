package com.github.kolegran.trackdebtsemailservice;

import org.apache.commons.mail.*;
import org.apache.commons.mail.HtmlEmail;

import java.io.*;
import java.util.List;

public class SmtpExample {

    public static void emailSender(UserLedger userLedger) throws Exception {

        HtmlEmail email = new HtmlEmail();

        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("smtp.test.kolegran@gmail.com", "1qazXsw2"));
        email.setSSL(true);

        email.setFrom("smtp.test.kolegran@gmail.com", "Sender");
        email.addTo(userLedger.getUserEmail(), "Recipient");

        email.setSubject("Track debts");

        String html = readContentFromResource("email.html");
        html = html.replace("DEBTOR_FULL_NAME", userLedger.getFullName());
        String listOfDebts = getListOfDebts(userLedger.getUserBalanceList());
        html = html.replace("LIST_OF_DEBTS", listOfDebts);
        html = html.replace("TOTAL_DEBTS", userLedger.totalDebt().toString());

        email.setHtmlMsg(html);
        email.setTextMsg("Track debts");

        System.out.println("Email is sending...");
        email.send();
        System.out.println(html);
        System.out.println("Email sent");
    }

    private static String getListOfDebts(List<UserBalance> userBalanceList) {

        String res = "";
        for (UserBalance userBalance : userBalanceList) {
            res += "Your debtor is: " + userBalance.getFullName() + ". The debt is: " + userBalance.getAmount() + "<br>\n";
        }
        return res;
    }

    private static String readContentFromResource(String resourceName) throws IOException {
        InputStream is = SmtpExample.class.getClassLoader().getResourceAsStream(resourceName);

        return new String(new BufferedInputStream(is).readAllBytes());
    }
}
