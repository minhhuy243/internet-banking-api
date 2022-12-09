package com.internetbanking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    public Boolean sendMessage(String recipientEmail, Integer otpValue)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(emailDTO.getRecipients().stream().collect(Collectors.joining(",")));
//        mailMessage.setSubject(emailDTO.getSubject());
//        mailMessage.setText(emailDTO.getBody());

        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject("TEST OTP");
        mailMessage.setText(otpValue.toString());

        Boolean isSent = false;
        try
        {
            emailSender.send(mailMessage);
            isSent = true;
        } catch (Exception e) {
            log.error("Sending e-mail error: {}", e.getMessage());
        }
        return isSent;
    }


}
