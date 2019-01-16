package com.github.kolegran.trackdebtsemailservice;

import org.apache.commons.mail.*;
import org.apache.commons.mail.HtmlEmail;

import java.io.*;
import java.util.List;

public class NotificationService {

    private String hostName;
    private int port;
    private String userName;
    private String password;
    private boolean ssl;

    public NotificationService(String hostName, int port, String userName, String password, boolean ssl) {
        this.hostName = hostName;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.ssl = ssl;
    }

    public void send(UserLedger userLedger) {
        try {
            HtmlEmail email = new HtmlEmail();

            email.setHostName(hostName);
            email.setSmtpPort(port);
            email.setAuthenticator(new DefaultAuthenticator(userName, password));
            email.setSSL(ssl);

            email.setFrom(userName, "Sender");
            email.addTo(userLedger.getUser().getEmail(), "Recipient");

            email.setSubject("Track debts");

            String html = readContentFromResource("email.html");

            html = html.replace("DEBTOR_FULL_NAME", userLedger.getUser().getDisplayName());
            String listOfDebts = getListOfDebts(userLedger.getUserBalanceList());
            html = html.replace("LIST_OF_DEBTS", listOfDebts);
            html = html.replace("TOTAL_DEBTS", userLedger.totalDebt().toString());

            email.setHtmlMsg(html);
            email.setTextMsg("Track debts");

            System.out.println("Email is sending...");
            email.send();
            System.out.println(html);
            System.out.println("Email sent");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String getListOfDebts(List<UserBalance> userBalanceList) {
        String res = "";
        for (UserBalance userBalance : userBalanceList) {
            res += "You owe to " + userBalance.getUser().getDisplayName() + " " + userBalance.getAmount() + " uah.<br>\n";
        }
        return res;
    }

    private String readContentFromResource(String resourceName) throws IOException {
        InputStream is = NotificationService.class.getClassLoader().getResourceAsStream(resourceName);

        return new String(new BufferedInputStream(is).readAllBytes());
    }
}
