package dev.alpey.reliabill.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import dev.alpey.reliabill.model.dto.finance.InvoiceTaxDetails;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.UserRepository;
import dev.alpey.reliabill.service.email.EmailService;
import dev.alpey.reliabill.utils.TaxCalculation;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EmailService emailService;

    public InputStream generateInvoicePdf(String username, Long invoiceId) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        InvoiceTaxDetails invoiceTaxDetails = TaxCalculation.getInvoiceTaxDetails(invoice);
        Company userCompany = companyRepository.findById(user.getCompanyId()).orElseThrow();
        Company clientCompany = invoice.getCompany();
        List<Item> items = invoice.getItems();

        ZoneId belgradeTimeZone = ZoneId.of("Europe/Belgrade");
        LocalDateTime currentDateTime = LocalDateTime.now(belgradeTimeZone);
        String formattedDateTime = currentDateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

        emailService.sendEmailToAdmin("""
                        PDF invoice generated
                        """,
                """
                        User %s with username %s generated an invoice %s.
                        Invoice total is: %s.
                        Local time: %s.
                        """.formatted(
                        user.getName(),
                        user.getUsername(),
                        invoice.getInvoiceNumber(),
                        invoice.getTotal(),
                        formattedDateTime
                ));

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("userCompany", userCompany);
        context.setVariable("clientCompany", clientCompany);
        context.setVariable("invoice", invoice);
        context.setVariable("invoiceTaxDetails", invoiceTaxDetails);
        context.setVariable("items", items);

        String htmlContent = templateEngine.process("invoice", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont(
                "src/main/resources/fonts/dejaVuSans/DejaVuSans.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.NOT_EMBEDDED);
        renderer.setDocumentFromString(htmlContent, null);
        renderer.layout();
        renderer.createPDF(out);

        return new ByteArrayInputStream(out.toByteArray());
    }
}
