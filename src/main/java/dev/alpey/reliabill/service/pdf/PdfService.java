package dev.alpey.reliabill.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
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

import dev.alpey.reliabill.enums.DocumentType;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class PdfService {

    public static final int NUM_COLUMNS = 8;
    public static final int FOUR_COLUMNS = 4;
    public static final int WIDTH_PERCENTAGE = 100;
    public static final int SPACING_BEFORE = 10;
    public static final int SPACING_AFTER = 10;
    public static final int FONT_SIZE = 8;
    public static final int LARGE_FONT_SIZE = 16;
    public static final int NUM_COLUMNS_SIGNATURES = 3;
    public static final int COMPANY_INFO_COLUMNS = 1;
    public static final int TOTAL_INFO_COLUMNS = 8;
    public static final int ALIGN_RIGHT = Element.ALIGN_RIGHT;
    public static final int ALIGN_LEFT = Element.ALIGN_LEFT;
    public static final Font DEFAULT_FONT = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.NORMAL);
    public static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.BOLD);
    public static final Font LARGE_FONT = new Font(Font.FontFamily.HELVETICA, LARGE_FONT_SIZE, Font.BOLD);
    public static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.GRAY);
    public static final int NUMBER_OF_EMPTY_CELLS = 6;
    public static final int PRODUCT_COLUMN_WIDTH = 6;
    public static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final int MEDIUM_COLUMN_WIDTH = 3;

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

        Paragraph clientHeading = new Paragraph("Klijent", BOLD_FONT);
        Paragraph separationLine = new Paragraph("__________________________________________________", SMALL_FONT);

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

        // Add the total info paragraph to the document
        PdfPTable totalInfo = getTotalInfo(invoice);
        document.add(totalInfo);

        document.add(new Paragraph("\n"));

        // Add tax details per tax rate info
        PdfPTable taxDetailsPerTaxRateInfo = getTaxDetailsPerTaxRateInfo(invoice);
        document.add(taxDetailsPerTaxRateInfo);

        // Show message if user is not in VAT system
        if (!user.getVatStatus()) {

            document.add(new Paragraph("\n"));

            Paragraph noteHeading = new Paragraph("Beleska", BOLD_FONT);
            Paragraph noteSeparationLine = new Paragraph(
                    "_________________________________________________________________",
                    SMALL_FONT);
            Paragraph noteMessage = new Paragraph(
                    "PDV nije obracunat u skladu sa clanom 24. Zakona o porezu na dodatu vrednost.",
                    SMALL_FONT);

            document.add(noteHeading);
            document.add(noteSeparationLine);
            document.add(noteMessage);
        }

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        // Signatures
        PdfPTable signatureTable = getSignatureTable();
        document.add(signatureTable);

        document.add(new Paragraph("\n"));

        // Legal disclaimer
        Paragraph legalDisclaimer = new Paragraph(new Phrase(
                "Za zakasnelo placanje obracunava se zakonska zatezna kamata. "
                        + "U slucaju spora odgovoran je nadlezni sud.",
                SMALL_FONT
        ));
        legalDisclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(legalDisclaimer);

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
        DocumentType documentType = invoice.getDocumentType();
        String documentTypeToSerbian = documentType.equals(DocumentType.INVOICE) ? "Faktura" : "Profaktura";
        Paragraph heading = new Paragraph(
                documentTypeToSerbian + " " + invoice.getInvoiceNumber(), LARGE_FONT);
        heading.setAlignment(Element.ALIGN_LEFT);

        PdfPTable dateTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        dateTable.setWidthPercentage(WIDTH_PERCENTAGE);

        DateTimeFormatter serbianDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

        LocalDate creationDate = invoice.getCreationDate();
        String formattedCreationDate = creationDate.format(serbianDateFormat);

        LocalDate dueDate = invoice.getDueDate();
        String formattedDueDate = dueDate.format(serbianDateFormat);

        PdfPCell creationDateCell = new PdfPCell(new Phrase(
                "Datum izdavanja: " + formattedCreationDate, DEFAULT_FONT));
        creationDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        creationDateCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell dueDateCell = new PdfPCell(new Phrase(
                "Datum dospeca: " + formattedDueDate, DEFAULT_FONT));
        dueDateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        dueDateCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell issueLocationCell = new PdfPCell(new Phrase(
                "Mesto izdavanja: "
                        + invoice.getCompany().getCity(), DEFAULT_FONT));
        issueLocationCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        issueLocationCell.setBorder(Rectangle.NO_BORDER);

        dateTable.addCell(creationDateCell);
        dateTable.addCell(dueDateCell);
        dateTable.addCell(issueLocationCell);

        heading.add(dateTable);
        return heading;
    }

    private static PdfPTable getItemsTable(Set<Item> items) throws DocumentException {
        // Invoice items table
        PdfPTable table = new PdfPTable(NUM_COLUMNS);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        table.setSpacingBefore(SPACING_BEFORE);
        table.setSpacingAfter(SPACING_AFTER);

        float[] columnWidths = {
                1, // number
                PRODUCT_COLUMN_WIDTH, // product name
                2, // amount
                MEDIUM_COLUMN_WIDTH, // price
                1, //VAT
                MEDIUM_COLUMN_WIDTH, // pre tax price
                MEDIUM_COLUMN_WIDTH, // total
                MEDIUM_COLUMN_WIDTH // subtotal
        };
        table.setWidths(columnWidths);

        PdfPCell tableHeaderCell0 = new PdfPCell(new Phrase("#", BOLD_FONT));
        PdfPCell tableHeaderCell1 = new PdfPCell(new Phrase("Artikal", BOLD_FONT));
        PdfPCell tableHeaderCell2 = new PdfPCell(new Phrase("Kolicina", BOLD_FONT));
        PdfPCell tableHeaderCell3 = new PdfPCell(new Phrase("Cena", BOLD_FONT));
        PdfPCell tableHeaderCell4 = new PdfPCell(new Phrase("PDV %", BOLD_FONT));
        PdfPCell tableHeaderCell5 = new PdfPCell(new Phrase("Cena bez PDV-a", BOLD_FONT));
        PdfPCell tableHeaderCell6 = new PdfPCell(new Phrase("Iznos sa PDV-om", BOLD_FONT));
        PdfPCell tableHeaderCell7 = new PdfPCell(new Phrase("Iznos bez PDV-a", BOLD_FONT));

        tableHeaderCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeaderCell7.setHorizontalAlignment(Element.ALIGN_CENTER);

        tableHeaderCell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableHeaderCell7.setBackgroundColor(BaseColor.LIGHT_GRAY);

        table.addCell(tableHeaderCell0);
        table.addCell(tableHeaderCell1);
        table.addCell(tableHeaderCell2);
        table.addCell(tableHeaderCell3);
        table.addCell(tableHeaderCell4);
        table.addCell(tableHeaderCell5);
        table.addCell(tableHeaderCell6);
        table.addCell(tableHeaderCell7);

        int num = 1;

        for (Item item : items) {
            PdfPCell cell0 = new PdfPCell(new Phrase(Integer.toString(num++), DEFAULT_FONT));
            PdfPCell cell1 = new PdfPCell(new Phrase(item.getProductName(), DEFAULT_FONT));
            PdfPCell cell2 = new PdfPCell(new Phrase(TWO_DECIMAL_FORMAT.format(item.getQuantity()), DEFAULT_FONT));
            PdfPCell cell3 = new PdfPCell(new Phrase(TWO_DECIMAL_FORMAT.format(item.getPrice()), DEFAULT_FONT));
            PdfPCell cell4 = new PdfPCell(new Phrase(item.getTaxRate().getRate() + " %", DEFAULT_FONT));
            PdfPCell cell5 = new PdfPCell(new Phrase(TWO_DECIMAL_FORMAT.format(item.getPreTax()), DEFAULT_FONT));
            PdfPCell cell6 = new PdfPCell(new Phrase(TWO_DECIMAL_FORMAT.format(item.getTotal()), DEFAULT_FONT));
            PdfPCell cell7 = new PdfPCell(new Phrase(TWO_DECIMAL_FORMAT.format(item.getSubtotal()), DEFAULT_FONT));

            cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(cell0);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            table.addCell(cell7);
        }
        return table;
    }

    private static PdfPTable getTotalInfo(Invoice invoice) {

        PdfPTable totalInfoTable = new PdfPTable(TOTAL_INFO_COLUMNS);
        totalInfoTable.setWidthPercentage(WIDTH_PERCENTAGE);
        totalInfoTable.getDefaultCell().setHorizontalAlignment(ALIGN_RIGHT);
        totalInfoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        for (int i = 0; i < NUMBER_OF_EMPTY_CELLS; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("Ukupno", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(TWO_DECIMAL_FORMAT.format(invoice.getSubtotal()), BOLD_FONT));

        for (int i = 0; i < NUMBER_OF_EMPTY_CELLS; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("PDV", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(TWO_DECIMAL_FORMAT.format(invoice.getTax()), BOLD_FONT));

        for (int i = 0; i < NUMBER_OF_EMPTY_CELLS; i++) {
            totalInfoTable.addCell(new Phrase(""));
        }
        totalInfoTable.addCell(new Phrase("Za placanje:", BOLD_FONT));
        totalInfoTable.addCell(new Phrase(TWO_DECIMAL_FORMAT.format(invoice.getTotal()), BOLD_FONT));

        return totalInfoTable;
    }

    private static PdfPTable getTaxDetailsPerTaxRateInfo(Invoice invoice) throws DocumentException {

        PdfPTable taxDetailsPerTaxRateTable = new PdfPTable(FOUR_COLUMNS);
        taxDetailsPerTaxRateTable.setWidthPercentage(WIDTH_PERCENTAGE);
        taxDetailsPerTaxRateTable.getDefaultCell().setHorizontalAlignment(ALIGN_RIGHT);

        float[] columnWidths = {2, 1, 1, 1};
        taxDetailsPerTaxRateTable.setWidths(columnWidths);

        PdfPCell descriptionCell = new PdfPCell(new Phrase("Poreska stopa", BOLD_FONT));
        descriptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        descriptionCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        taxDetailsPerTaxRateTable.addCell(descriptionCell);

        PdfPCell tableCell1 = new PdfPCell(new Phrase("Osnova", BOLD_FONT));
        PdfPCell tableCell2 = new PdfPCell(new Phrase("PDV", BOLD_FONT));
        PdfPCell tableCell3 = new PdfPCell(new Phrase("Iznos", BOLD_FONT));

        tableCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableCell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tableCell3.setBackgroundColor(BaseColor.LIGHT_GRAY);

        taxDetailsPerTaxRateTable.addCell(tableCell1);
        taxDetailsPerTaxRateTable.addCell(tableCell2);
        taxDetailsPerTaxRateTable.addCell(tableCell3);

        if (invoice.getTotalFor20() > 0) {
            PdfPCell taxRate20Cell = new PdfPCell(new Phrase("20 %", DEFAULT_FONT));
            taxRate20Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            taxDetailsPerTaxRateTable.addCell(taxRate20Cell);
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getSubtotalFor20()),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTaxFor20()),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTotalFor20()),
                    DEFAULT_FONT));
        }

        if (invoice.getTotalFor10() > 0) {
            PdfPCell taxRate10Cell = new PdfPCell(new Phrase("10 %", DEFAULT_FONT));
            taxRate10Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            taxDetailsPerTaxRateTable.addCell(taxRate10Cell);
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getSubtotalFor10()),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTaxFor10()),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTotalFor10()),
                    DEFAULT_FONT));
        }

        if (invoice.getTotalFor0() > 0) {
            PdfPCell taxRate0Cell = new PdfPCell(new Phrase("0 %", DEFAULT_FONT));
            taxRate0Cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            taxDetailsPerTaxRateTable.addCell(taxRate0Cell);
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTotalFor0()),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(0),
                    DEFAULT_FONT));
            taxDetailsPerTaxRateTable.addCell(new Phrase(
                    TWO_DECIMAL_FORMAT.format(invoice.getTotalFor0()),
                    DEFAULT_FONT));
        }

        return taxDetailsPerTaxRateTable;
    }

    private static PdfPTable getSignatureTable() {
        // Create table for signatures and stamp
        PdfPTable signatureTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        signatureTable.setWidthPercentage(WIDTH_PERCENTAGE);
        signatureTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // Add issuer signature cell to table
        PdfPCell issuerSignatureCell = new PdfPCell(new Phrase("Fakturisao: "));
        issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
        issuerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.addCell(issuerSignatureCell);

        signatureTable.addCell(new Phrase(""));

        // Add client signature cell to table
        PdfPCell clientSignatureCell = new PdfPCell(new Phrase("Primio: "));
        clientSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        clientSignatureCell.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(clientSignatureCell);

        signatureTable.addCell(new Phrase(""));
        signatureTable.addCell(new Phrase(""));
        signatureTable.addCell(new Phrase(""));

        signatureTable.addCell(new Phrase("_________________________"));
        signatureTable.addCell(new Phrase(""));
        signatureTable.addCell(new Phrase("_________________________"));
        return signatureTable;
    }
}
