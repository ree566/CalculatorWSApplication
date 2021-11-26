/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.User;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 *
 * @author Wei.Cheng Regist by spring context
 */
public class MailManager {

    private static final Logger log = LoggerFactory.getLogger(MailManager.class);

    private JavaMailSender mailSender;

    private String hostName;

    @Value("${send.mail.alarm.user: true}")
    private boolean sendMailAlarmUser;

    @PostConstruct
    protected void initHostName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            hostName = env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            hostName = env.get("HOSTNAME");
        } else {
            hostName = "Unknown";
        }
        hostName = hostName + "@advantech.com.tw";
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendMail(List<User> to, List<User> cc, String subject, String text) throws MessagingException {
        if (to.isEmpty()) {
            return false;
        }
        if (cc == null) {
            cc = new ArrayList();
        }
        String[] toMails = (String[]) to.stream().map(User::getEmail).collect(Collectors.toList()).stream().toArray(String[]::new);
        String[] ccMails = cc.stream().map(User::getEmail).collect(Collectors.toList()).stream().toArray(String[]::new);

        return sendMail(toMails, ccMails, subject, text);
    }

    public boolean sendMail(String[] to, String subject, String text) throws MessagingException {
        return this.sendMail(to, new String[0], subject, text);
    }

    public boolean sendMail(String[] to, String[] cc, String subject, String text) throws MessagingException {
        if (!sendMailAlarmUser) {
            return false;
        }

        // Do the business calculations...
        // Call the collaborators to persist the order...
        // Create a thread safe "copy" of the template message and customize it
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        helper.setText(text, true);
        helper.setTo(to);
        helper.setCc(cc);
        helper.setSubject(subject);
        helper.setFrom(hostName);

        try {
            this.mailSender.send(mimeMessage);
            return true;
        } catch (MailException ex) {
            // simply log it and go on...
            log.error(ex.getMessage());
            return false;
        }
    }

    //Get the Host address.
    private String getHostAddr() throws UnknownHostException, SocketException {
        return InetAddress.getLocalHost().getHostAddress();  // often returns "127.0.0.1"
    }
}
