package org.kreyzon.stripe.service;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl implements EmailService{
    //    private final JavaMailSender javaMailSender;
//
//    @Autowired
//    public EmailServiceImpl(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//    public void sendEmail(String to, String subject, String body) throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(body, true); // true indicates HTML content
//        javaMailSender.send(message);
//    }
    private MailProperties mailProperties;
    //public boolean sendEmail(String subject, String message, String to) {
//    try {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(mailProperties.getUsername());
//        mailMessage.setTo(to);
//        mailMessage.setSubject(subject);
//        mailMessage.setText(message);
//        mailSender.send(mailMessage);
//        System.out.println("Email sent successfully...");
//        return true;
//    } catch (Exception e) {
//        e.printStackTrace();
//        return false;
//    }
//}
    public void printMailProperties() {
        String host = mailProperties.getHost();
        int port = mailProperties.getPort();
        String username = mailProperties.getUsername();
        String password = mailProperties.getPassword();
        boolean smtpAuth = Boolean.parseBoolean(mailProperties.getProperties().get("mail.smtp.auth"));
        boolean smtpStarttlsEnable = Boolean.parseBoolean(mailProperties.getProperties().get("mail.smtp.starttls.enable"));

        System.out.println("SMTP Host: " + host);
        System.out.println("SMTP Port: " + port);
        System.out.println("SMTP Username: " + username);
        System.out.println("SMTP Password: " + password);
        System.out.println("SMTP Auth: " + smtpAuth);
        System.out.println("SMTP StartTLS Enable: " + smtpStarttlsEnable);
    }
}
