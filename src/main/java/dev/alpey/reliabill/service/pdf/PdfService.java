package dev.alpey.reliabill.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

import dev.alpey.reliabill.enums.CompanyBalanceSortBy;
import dev.alpey.reliabill.enums.ProductSortBy;
import dev.alpey.reliabill.model.dto.finance.CompanyBalance;
import dev.alpey.reliabill.model.dto.finance.InvoiceTaxDetails;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;
import dev.alpey.reliabill.model.entity.Product;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.ProductRepository;
import dev.alpey.reliabill.repository.UserRepository;
import dev.alpey.reliabill.service.CompanyService;
import dev.alpey.reliabill.service.ProductService;
import dev.alpey.reliabill.utils.TaxCalculation;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public InputStream generateInvoicePdf(String username, Long invoiceId) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        InvoiceTaxDetails invoiceTaxDetails = TaxCalculation.getInvoiceTaxDetails(invoice);
        Company userCompany = companyRepository.findById(user.getCompanyId()).orElseThrow();
        Company clientCompany = invoice.getCompany();
        List<Item> items = invoice.getItems();

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("userCompany", userCompany);
        context.setVariable("clientCompany", clientCompany);
        context.setVariable("invoice", invoice);
        context.setVariable("invoiceTaxDetails", invoiceTaxDetails);
        context.setVariable("items", items);

        return getInputStream("invoice", context);
    }

    public InputStream generateCompaniesReport(String username, String sortBy) throws Exception {
        List<Company> companies = companyRepository.findByUsername(username);
        List<CompanyBalance> sortedCompanyBalances = companyService
                .sort(companies, CompanyBalanceSortBy.valueOf(sortBy));

        Context context = new Context();
        context.setVariable("companyBalances", sortedCompanyBalances);

        return getInputStream("companiesReport", context);
    }

    public InputStream generateProductsReport(String username, String sortBy) throws Exception {
        List<Product> products = productRepository.findByUsername(username).stream()
                .filter(product -> product.getUnitsSold() > 0)
                .toList();
        List<Product> sortedProducts = productService
                .sort(products, ProductSortBy.valueOf(sortBy));

        Context context = new Context();
        context.setVariable("products", sortedProducts);

        return getInputStream("productsReport", context);
    }

    private ByteArrayInputStream getInputStream(String templateName,
                                                Context context) throws DocumentException, IOException {
        String htmlContent = templateEngine.process(templateName, context);

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
