package com.techdevweb.techdevbackend.Notification.Service;

public interface MailService {
    void send(String to, String subject, String body);
}
