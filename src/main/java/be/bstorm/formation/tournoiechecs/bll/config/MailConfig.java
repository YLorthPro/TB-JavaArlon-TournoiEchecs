package be.bstorm.formation.tournoiechecs.bll.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.swing.*;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String mailServerHost;

    @Value("${spring.mail.port}")
    private int mailServerPort;


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);

        return mailSender;
    }
}
