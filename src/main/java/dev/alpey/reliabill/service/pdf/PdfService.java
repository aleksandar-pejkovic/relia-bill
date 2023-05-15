package dev.alpey.reliabill.service.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
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
    public static final int NUM_COLUMNS_SIGNATURES = 3;

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
        Font defaultFont = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.NORMAL);

        document.open();

        // User's company data
        PdfPTable userTable = new PdfPTable(1);
        userTable.setWidthPercentage(100);
        userTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        userTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        userTable.addCell(new Phrase(userCompany.getName(),
                new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.BOLD)));
        userTable.addCell(new Phrase(userCompany.getStreet(), defaultFont));
        userTable.addCell(new Phrase(userCompany.getZip() + " " + userCompany.getCity(), defaultFont));
        userTable.addCell(new Phrase("Reg. number/" + userCompany.getRegistrationNumber(), defaultFont));
        userTable.addCell(new Phrase("Tax number/" + userCompany.getTaxNumber(), defaultFont));
        userTable.addCell(new Phrase(userCompany.getEmail(), defaultFont));
        userTable.addCell(new Phrase(userCompany.getPhone(), defaultFont));
        userTable.addCell(new Phrase("Bank account/" + userCompany.getBankAccount(), defaultFont));

        document.add(userTable);

        // Client's company data
        PdfPTable clientTable = new PdfPTable(5);
        clientTable.setWidthPercentage(100);
        clientTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        clientTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        clientTable.addCell(new Phrase("Name", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getName(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Street", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getStreet(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("City", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getCity(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Registration Number", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getRegistrationNumber(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Tax Number", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getTaxNumber(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Phone", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getPhone(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Email", defaultFont));
        clientTable.addCell(new Phrase(clientCompany.getEmail(), defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase("Director", defaultFont));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(""));
        clientTable.addCell(new Phrase(clientCompany.getDirector(), defaultFont));

        document.add(clientTable);

        // Invoice heading
        Paragraph heading = new Paragraph(
                invoice.getDocumentType().getType() + ": " + invoice.getInvoiceNumber()
                        + " - Date: " + invoice.getCreationDate()
                        + " - Due date: " + invoice.getDueDate());
        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);
        // Invoice items table
        PdfPTable table = new PdfPTable(NUM_COLUMNS);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        table.setSpacingBefore(SPACING_BEFORE);
        table.setSpacingAfter(SPACING_AFTER);

        PdfPCell tableHeaderCell1 = new PdfPCell(new Phrase("Product Name", defaultFont));
        PdfPCell tableHeaderCell2 = new PdfPCell(new Phrase("Unit", defaultFont));
        PdfPCell tableHeaderCell3 = new PdfPCell(new Phrase("Quantity", defaultFont));
        PdfPCell tableHeaderCell4 = new PdfPCell(new Phrase("Price", defaultFont));
        PdfPCell tableHeaderCell5 = new PdfPCell(new Phrase("VAT rate", defaultFont));
        PdfPCell tableHeaderCell6 = new PdfPCell(new Phrase("Pre tax", defaultFont));
        PdfPCell tableHeaderCell7 = new PdfPCell(new Phrase("Subtotal", defaultFont));
        PdfPCell tableHeaderCell8 = new PdfPCell(new Phrase("Tax", defaultFont));
        PdfPCell tableHeaderCell9 = new PdfPCell(new Phrase("Total", defaultFont));

        table.addCell(tableHeaderCell1);
        table.addCell(tableHeaderCell2);
        table.addCell(tableHeaderCell3);
        table.addCell(tableHeaderCell4);
        table.addCell(tableHeaderCell5);
        table.addCell(tableHeaderCell6);
        table.addCell(tableHeaderCell7);
        table.addCell(tableHeaderCell8);
        table.addCell(tableHeaderCell9);

        for (Item item : items) {
            PdfPCell cell1 = new PdfPCell(new Phrase(item.getProductName()));
            PdfPCell cell2 = new PdfPCell(new Phrase(item.getUnit()));
            PdfPCell cell3 = new PdfPCell(new Phrase(Double.toString(item.getQuantity())));
            PdfPCell cell4 = new PdfPCell(new Phrase(Double.toString(item.getPrice())));
            PdfPCell cell5 = new PdfPCell(new Phrase(Integer.toString(item.getTaxRate().getRate())));
            PdfPCell cell6 = new PdfPCell(new Phrase(Double.toString(item.getPreTax())));
            PdfPCell cell7 = new PdfPCell(new Phrase(Double.toString(item.getSubtotal())));
            PdfPCell cell8 = new PdfPCell(new Phrase(Double.toString(item.getTax())));
            PdfPCell cell9 = new PdfPCell(new Phrase(Double.toString(item.getTotal())));

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            table.addCell(cell7);
            table.addCell(cell8);
            table.addCell(cell9);
        }
        document.add(table);

        // Define a key-pair style format for displaying the subtotal, tax, and total values
        Font keyPairFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Chunk subtotalChunk = new Chunk("Subtotal: ", keyPairFont);
        Chunk taxChunk = new Chunk("Tax: ", keyPairFont);
        Chunk totalChunk = new Chunk("Total: ", keyPairFont);
        Chunk subtotalValueChunk = new Chunk(Double.toString(invoice.getSubtotal()));
        Chunk taxValueChunk = new Chunk(Double.toString(invoice.getTax()));
        Chunk totalValueChunk = new Chunk(Double.toString(invoice.getTotal()));
        subtotalValueChunk.setFont(keyPairFont);
        taxValueChunk.setFont(keyPairFont);
        totalValueChunk.setFont(keyPairFont);

        // Create a new paragraph to hold the key-pair style elements and align them on the right
        Paragraph keyPairParagraph = new Paragraph();
        keyPairParagraph.add(subtotalChunk);
        keyPairParagraph.add(subtotalValueChunk);
        keyPairParagraph.add(Chunk.NEWLINE);
        keyPairParagraph.add(taxChunk);
        keyPairParagraph.add(taxValueChunk);
        keyPairParagraph.add(Chunk.NEWLINE);
        keyPairParagraph.add(totalChunk);
        keyPairParagraph.add(totalValueChunk);
        keyPairParagraph.setAlignment(Element.ALIGN_RIGHT);

        // Add the key-pair style paragraph to the document
        document.add(keyPairParagraph);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        // Create table for signatures and stamp
        PdfPTable signatureTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        signatureTable.setWidthPercentage(WIDTH_PERCENTAGE);
        // Add issuer signature cell to table
        PdfPCell issuerSignatureCell = new PdfPCell(new Phrase("Issuer signature: _________________________"));
        issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(issuerSignatureCell);
        // Add stamp cell to table
        PdfPCell stampCell = new PdfPCell(new Phrase("Stamp: "));
        stampCell.setBorder(Rectangle.NO_BORDER);
        stampCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.addCell(stampCell);
        // Add client signature cell to table
        PdfPCell clientSignatureCell = new PdfPCell(new Phrase("Client signature: _________________________"));
        clientSignatureCell.setBorder(Rectangle.NO_BORDER);
        clientSignatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        signatureTable.addCell(clientSignatureCell);
        // Add signature table to document
        document.add(signatureTable);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
