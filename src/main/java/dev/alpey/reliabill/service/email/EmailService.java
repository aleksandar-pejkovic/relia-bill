package dev.alpey.reliabill.service.email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.repository.RoleRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Async
    public void sendEmailToAdmin(String subject, String text) {
        List<String> adminEmailsList = userRepository.findAdminEmails();
        String[] adminEmails = adminEmailsList.toArray(new String[0]);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(adminEmails);
        message.setSubject(subject);
        message.setText(text);
        try {
            emailSender.send(message);
        } catch (MailSendException e) {
            System.out.println("Mail server connection failed. Email was not sent to admin(s)!");
        }
    }
}
