package org.example.stackoverflowsd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(
            String to /*String subject, String text*/) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ai fost banat vere");
        message.setText("Ceva prostii ai facut tu pe acolo");
        try
        {
            emailSender.send(message);
        }
        catch (Exception e)
        {
            System.out.println("Error in EmailService: " + e.getMessage());
        }
    }
}
