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
    private final SecurityService securityService;

    public Boolean sendMessage(String recipientEmail, Integer otpValue, String type)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        String content = "";
        if (type.equalsIgnoreCase("TRANSACTION")) {
            mailMessage.setSubject("Xác nhận giao dịch - Internet Banking");
            content = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                    "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                    "    <div style=\"border-bottom:1px solid #eee\">\n" +
                    "      <a href=\"\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Xác nhận giao dịch</a>\n" +
                    "    </div>\n" +
                    "    <p style=\"font-size:1.1em\">Hi, " + securityService.getFullName() + "</p>\n" +
                    "    <p>Cảm ơn quý khách đã tin tưởng sử dụng dịch vụ của chúng tôi. Sử dụng mã OTP dưới đây để xác nhận giao dịch. Mã OTP có thời hạn trong 5 phút. Quý khách tuyệt đối không chia sẻ để tránh bị lừa đảo.</p>\n" +
                    "    <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + otpValue.toString() + "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Trân trọng,<br />Internet Banking</p>\n" +
                    "  </div>\n" +
                    "</div>";
        } else if (type.equalsIgnoreCase("FORGOT_PASSWORD")) {
            mailMessage.setSubject("Quên mật khẩu - Internet Banking");
            content = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                    "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                    "    <div style=\"border-bottom:1px solid #eee\">\n" +
                    "      <a href=\"\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Quên mật khẩu</a>\n" +
                    "    </div>\n" +
                    "    <p style=\"font-size:1.1em\">Hi, " + securityService.getFullName() + "</p>\n" +
                    "    <p>Cảm ơn quý khách đã tin tưởng sử dụng dịch vụ của chúng tôi. Sử dụng mã OTP dưới đây để lấy lại mật khẩu. Mã OTP có thời hạn trong 5 phút. Quý khách tuyệt đối không chia sẻ để tránh bị lừa đảo.</p>\n" +
                    "    <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + otpValue.toString() + "</h2>\n" +
                    "    <p style=\"font-size:0.9em;\">Trân trọng,<br />Internet Banking</p>\n" +
                    "  </div>\n" +
                    "</div>";
        }
        mailMessage.setText(content);
        Boolean isSent = false;
        try {
            emailSender.send(mailMessage);
            isSent = true;
        } catch (Exception e) {
            log.error("Sending e-mail error: {}", e.getMessage());
        }
        return isSent;
    }
}
