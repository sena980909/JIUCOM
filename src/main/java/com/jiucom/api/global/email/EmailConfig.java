package com.jiucom.api.global.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class EmailConfig {

    @Bean
    @Profile({"dev", "test", "default"})
    public EmailService mockEmailService() {
        return new MockEmailService();
    }

    @Bean
    @Profile("prod")
    @ConditionalOnProperty(name = "email.enabled", havingValue = "true")
    public EmailService smtpEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        return new SmtpEmailService(mailSender, templateEngine);
    }
}
