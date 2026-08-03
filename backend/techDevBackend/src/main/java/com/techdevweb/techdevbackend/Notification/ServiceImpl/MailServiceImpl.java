package com.techdevweb.techdevbackend.Notification.ServiceImpl;

import com.techdevweb.techdevbackend.Notification.Service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("noreply@techdev.com");
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email gönderildi: {} - {}", to, subject);
        } catch (Exception e) {
            // Email gönderimi başarısız olsa bile ana işlemi (kayıt, onay vs.) etkilemesin
            log.error("Email gönderilemedi: {} - {}", to, e.getMessage());
        }
    }
}
