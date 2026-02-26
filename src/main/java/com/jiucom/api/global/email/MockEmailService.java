package com.jiucom.api.global.email;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        log.info("[MockEmail] To: {}, Subject: {}, Template: {}, Variables: {}", to, subject, template, variables);
    }
}
