package dev.alpey.reliabill.service.email;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.configuration.exceptions.company.CompanyNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.RoleRepository;
import dev.alpey.reliabill.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

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

    @Async
    public void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        try {
            emailSender.send(message);
        } catch (MailSendException e) {
            System.out.println("Mail server connection failed. Email was not sent to admin(s)!");
        }
    }

    @Async
    public void sendInvoiceEmailToClient(Invoice invoice, InputStream inputStream, Principal principal)
            throws MessagingException, IOException {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        Company usersCompany = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));

        LocalDateTime creationDate = invoice.getCreationDate().atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.", new Locale("sr"));
        String formattedDate = creationDate.format(formatter);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String subject = invoice.getDocumentType().getType() + " " + invoice.getInvoiceNumber();
        String body = """
                                Poštovani,

                                Kreirana je %s za Vas od strane %s.

                                Detalji dokumanta:
                                Broj: %s
                                Datum: %s
                                Iznos: %s

                                PDF dokument se nalazi u prilogu ovog mejla.

                                Srdačan pozdrav!
                                %s

                                P.S Pošiljaoca možete kontaktirati klikom na "Reply"
                                https://reliabill.netlify.app/
                """.formatted(
                invoice.getDocumentType().getType().toLowerCase(),
                usersCompany.getName(),
                invoice.getInvoiceNumber(),
                formattedDate,
                invoice.getTotal(),
                usersCompany.getName()
        );

        helper.setFrom(usersCompany.getEmail());
        helper.setReplyTo(usersCompany.getEmail());
        helper.setTo(invoice.getCompany().getEmail());
        helper.setSubject(subject);
        helper.setText(body);

        String filename = subject + ".pdf";
        ByteArrayResource pdfInvoice = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        helper.addAttachment(filename, pdfInvoice);

        try {
            emailSender.send(message);
        } catch (MailSendException | IllegalArgumentException e) {
            System.out.println("Email was not sent!" + e.getMessage());
        }
    }
}
