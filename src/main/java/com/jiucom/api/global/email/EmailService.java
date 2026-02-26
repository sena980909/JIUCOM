package com.jiucom.api.global.email;

import java.util.Map;

public interface EmailService {

    void sendEmail(String to, String subject, String template, Map<String, Object> variables);
}
