package dev.alpey.reliabill.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class PdfService {

    public static final int NUM_COLUMNS = 9;
    public static final int WIDTH_PERCENTAGE = 100;
    public static final int SPACING_BEFORE = 10;
    public static final int SPACING_AFTER = 10;
    public static final int FONT_SIZE = 10;
    public static final int LARGE_FONT_SIZE = 16;
    public static final int NUM_COLUMNS_SIGNATURES = 3;
    public static final int COMPANY_INFO_COLUMNS = 1;
    public static final int TOTAL_INFO_COLUMNS = 9;
    public static final int ALIGN_RIGHT = Element.ALIGN_RIGHT;
    public static final int ALIGN_LEFT = Element.ALIGN_LEFT;
    public static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.NORMAL);
    public static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.BOLD);
    public static final Font LARGE_FONT = new Font(Font.FontFamily.HELVETICA, LARGE_FONT_SIZE, Font.BOLD);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public InputStream generateInvoicePdf(String username, Long invoiceId) throws DocumentException {
        User user = userRepository.findByUsername(username).orElseThrow();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        Company userCompany = companyRepository.findById(user.getCompanyId()).orElseThrow();
        Company clientCompany = invoice.getCompany();
        Set<Item> items = invoice.getItems();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // User's company data
        PdfPTable userTable = getCompanyInfoTable(userCompany, ALIGN_RIGHT);
        document.add(userTable);

        Paragraph separationLine = new Paragraph("_________________________");
        Paragraph clientHeading = new Paragraph("Klijent", BOLD_FONT);

        document.add(clientHeading);
        document.add(separationLine);
        document.add(new Paragraph("\n"));
        // Client's company data
        PdfPTable clientTable = getCompanyInfoTable(clientCompany, ALIGN_LEFT);
        document.add(clientTable);
        document.add(separationLine);

        // Invoice heading
        Paragraph heading = getHeading(invoice);
        document.add(heading);

        // Main table with items
        PdfPTable table = getItemsTable(items);
        document.add(table);

        PdfPTable totalInfo = getTotalInfo(invoice);

        // Add the total info paragraph to the document
        document.add(totalInfo);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        // Signatures and stamp
        PdfPTable signatureTable = getSignatureTable();
        document.add(signatureTable);

        // End
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static PdfPTable getCompanyInfoTable(Company company, int alignSide) {
        // Client's company data
        PdfPTable companyTable = new PdfPTable(COMPANY_INFO_COLUMNS);
        companyTable.setWidthPercentage(WIDTH_PERCENTAGE);
        companyTable.getDefaultCell().setHorizontalAlignment(alignSide);
        companyTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        companyTable.addCell(new Phrase(company.getName(), BOLD_FONT));
        companyTable.addCell(new Phrase(company.getStreet(), DEFAULT_FONT));
        companyTable.addCell(new Phrase(company.getZip() + " " + company.getCity(), DEFAULT_FONT));
        companyTable.addCell(new Phrase("MB: " + company.getRegistrationNumber(), DEFAULT_FONT));
        companyTable.addCell(new Phrase("PIB: " + company.getTaxNumber(), DEFAULT_FONT));
        companyTable.addCell(new Phrase(company.getPhone(), DEFAULT_FONT));
        companyTable.addCell(new Phrase(company.getEmail(), DEFAULT_FONT));
        companyTable.addCell(new Phrase("Tekuci racun: " + company.getBankAccount(), DEFAULT_FONT));
        return companyTable;
    }

    private static Paragraph getHeading(Invoice invoice) {
        Paragraph heading = new Paragraph(
                invoice.getDocumentType().getType() + ": " + invoice.getInvoiceNumber(), LARGE_FONT);
        heading.setAlignment(Element.ALIGN_CENTER);

        PdfPTable dateTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        dateTable.setWidthPercentage(WIDTH_PERCENTAGE);
        dateTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        dateTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        dateTable.addCell(new Phrase("Datum izdavanja: " + invoice.getCreationDate(), DEFAULT_FONT));
        dateTable.addCell(new Phrase(""));
        dateTable.addCell(new Phrase("Datum dospeca: " + invoice.getDueDate(), DEFAULT_FONT));
        heading.add(dateTable);
        return heading;
    }

    private static PdfPTable getItemsTable(Set<Item> items) {
        // Invoice items table
        PdfPTable table = new PdfPTable(NUM_COLUMNS);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        table.setSpacingBefore(SPACING_BEFORE);
        table.setSpacingAfter(SPACING_AFTER);

        PdfPCell tableHeaderCell0 = new PdfPCell(new Phrase("#", BOLD_FONT));
        PdfPCell tableHeaderCell1 = new PdfPCell(new Phrase("Artikal", BOLD_FONT));
        PdfPCell tableHeaderCell2 = new PdfPCell(new Phrase("JM", BOLD_FONT));
        PdfPCell tableHeaderCell3 = new PdfPCell(new Phrase("Kolicina", BOLD_FONT));
        PdfPCell tableHeaderCell4 = new PdfPCell(new Phrase("Cena", BOLD_FONT));
        PdfPCell tableHeaderCell5 = new PdfPCell(new Phrase("PDV %", BOLD_FONT));
        PdfPCell tableHeaderCell6 = new PdfPCell(new Phrase("Cena bez PDV-a", BOLD_FONT));
        PdfPCell tableHeaderCell7 = new PdfPCell(new Phrase("Iznos sa PDV-om", BOLD_FONT));
        PdfPCell tableHeaderCell8 = new PdfPCell(new Phrase("Iznos bez PDV-a", BOLD_FONT));

        table.addCell(tableHeaderCell0);
        table.addCell(tableHeaderCell1);
        table.addCell(tableHeaderCell2);
        table.addCell(tableHeaderCell3);
        table.addCell(tableHeaderCell4);
        table.addCell(tableHeaderCell5);
        table.addCell(tableHeaderCell6);
        table.addCell(tableHeaderCell7);
        table.addCell(tableHeaderCell8);

        int num = 1;
        for (Item item : items) {
            PdfPCell cell0 = new PdfPCell(new Phrase(Integer.toString(num++), DEFAULT_FONT));
            PdfPCell cell1 = new PdfPCell(new Phrase(item.getProductName(), DEFAULT_FONT));
            PdfPCell cell2 = new PdfPCell(new Phrase(item.getUnit()));
            PdfPCell cell3 = new PdfPCell(new Phrase(Double.toString(item.getQuantity()), DEFAULT_FONT));
            PdfPCell cell4 = new PdfPCell(new Phrase(Double.toString(item.getPrice()), DEFAULT_FONT));
            PdfPCell cell5 = new PdfPCell(new Phrase((item.getTaxRate().getRate()) + " %", DEFAULT_FONT));
            PdfPCell cell6 = new PdfPCell(new Phrase(Double.toString(item.getPreTax()), DEFAULT_FONT));
            PdfPCell cell7 = new PdfPCell(new Phrase(Double.toString(item.getTotal()), DEFAULT_FONT));
            PdfPCell cell8 = new PdfPCell(new Phrase(Double.toString(item.getSubtotal()), DEFAULT_FONT));

            table.addCell(cell0);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            table.addCell(cell7);
            table.addCell(cell8);
        }
        return table;
    }

    private static PdfPTable getTotalInfo(Invoice invoice) {

        PdfPTable totalInfoTable = new PdfPTable(TOTAL_INFO_COLUMNS);
        totalInfoTable.setWidthPercentage(WIDTH_PERCENTAGE);
        totalInfoTable.getDefaultCell().setHorizontalAlignment(ALIGN_RIGHT);
        totalInfoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        for (int i = 0; i < 7; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("Ukupno", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(Double.toString(invoice.getSubtotal()), BOLD_FONT));

        for (int i = 0; i < 7; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("PDV", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(Double.toString(invoice.getTax()), BOLD_FONT));

        for (int i = 0; i < 7; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("Za placanje:", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(Double.toString(invoice.getTotal()), BOLD_FONT));

        return totalInfoTable;
    }

    private static PdfPTable getSignatureTable() {
        // Create table for signatures and stamp
        PdfPTable signatureTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        signatureTable.setWidthPercentage(WIDTH_PERCENTAGE);
        signatureTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // Add issuer signature cell to table
        PdfPCell issuerSignatureCell = new PdfPCell(new Phrase("Fakturisao: "));
        issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(issuerSignatureCell);

        signatureTable.addCell(new Phrase(""));

        // Add client signature cell to table
        PdfPCell clientSignatureCell = new PdfPCell(new Phrase("Primio: "));
        clientSignatureCell.setHorizontalAlignment(ALIGN_RIGHT);
        clientSignatureCell.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(clientSignatureCell);

        signatureTable.addCell(new Phrase("_________________________"));
        signatureTable.addCell(new Phrase(""));
        signatureTable.addCell(new Phrase("_________________________"));
        return signatureTable;
    }
}
