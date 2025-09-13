package com.group01.aurora_demo.common.service;

import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import jakarta.mail.*;

public class EmailService {

    private final String username = "aurora.noreply.gr1@gmail.com";
    private final String appPassword = "vhac rkip fcqa qecx";

    private final Properties props;
    private final Session session;

    public EmailService() {
        props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        props.put("mail.smtp.pool", "true");
        props.put("mail.smtp.pool.size", "5");

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        props.put("mail.smtp.quitwait", "false");

        props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });
    }

    public boolean sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = new MimeMessage(session);

            final String fromPersonal = "Aurora - Beyond books, into everything";
            msg.setFrom(new InternetAddress(username, fromPersonal, "UTF-8"));

            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(html, "text/html; charset=UTF-8");
            msg.setHeader("Content-Transfer-Encoding", "8bit");

            Transport.send(msg);
            return true;

        } catch (SendFailedException e) {
            System.err.println("Địa chỉ email không hợp lệ hoặc từ chối: " + e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
        }
        return false;
    }
}