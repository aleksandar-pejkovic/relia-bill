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
    public static final int FONT_SIZE = 12;
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

        document.open();
        // Top left corner - Client's company data
        if (clientCompany != null) {
            Paragraph clientParagraph = new Paragraph(clientCompany.getName());
            clientParagraph.add(new Paragraph(clientCompany.getStreet()));
            clientParagraph.add(new Paragraph(clientCompany.getCity()));
            clientParagraph.add(new Paragraph(clientCompany.getRegistrationNumber()));
            clientParagraph.add(new Paragraph(clientCompany.getTaxNumber()));
            clientParagraph.add(new Paragraph(clientCompany.getPhone()));
            clientParagraph.add(new Paragraph(clientCompany.getEmail()));
            document.add(clientParagraph);
        }
        // Top right corner - User's company data
        Paragraph userParagraph = new Paragraph(userCompany.getName());
        userParagraph.add(new Paragraph(userCompany.getStreet()));
        userParagraph.add(new Paragraph(userCompany.getCity()));
        userParagraph.add(new Paragraph(userCompany.getRegistrationNumber()));
        userParagraph.add(new Paragraph(userCompany.getTaxNumber()));
        userParagraph.add(new Paragraph(userCompany.getBankAccount()));
        userParagraph.add(new Paragraph(userCompany.getPhone()));
        userParagraph.add(new Paragraph(userCompany.getEmail()));
        userParagraph.add(new Paragraph(userCompany.getDirector()));
        userParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(userParagraph);
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

        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, FONT_SIZE, Font.BOLD);
        PdfPCell tableHeaderCell1 = new PdfPCell(new Phrase("Product Name", tableHeaderFont));
        PdfPCell tableHeaderCell2 = new PdfPCell(new Phrase("Unit", tableHeaderFont));
        PdfPCell tableHeaderCell3 = new PdfPCell(new Phrase("Quantity", tableHeaderFont));
        PdfPCell tableHeaderCell4 = new PdfPCell(new Phrase("Price", tableHeaderFont));
        PdfPCell tableHeaderCell5 = new PdfPCell(new Phrase("VAT rate", tableHeaderFont));
        PdfPCell tableHeaderCell6 = new PdfPCell(new Phrase("Pre tax", tableHeaderFont));
        PdfPCell tableHeaderCell7 = new PdfPCell(new Phrase("Subtotal", tableHeaderFont));
        PdfPCell tableHeaderCell8 = new PdfPCell(new Phrase("Tax", tableHeaderFont));
        PdfPCell tableHeaderCell9 = new PdfPCell(new Phrase("Total", tableHeaderFont));

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
        // Define table header cells for subtotal, tax, and total columns
        PdfPCell subtotalHeaderCell = new PdfPCell(new Phrase("Subtotal", tableHeaderFont));
        PdfPCell taxHeaderCell = new PdfPCell(new Phrase("Tax", tableHeaderFont));
        PdfPCell totalHeaderCell = new PdfPCell(new Phrase("Total", tableHeaderFont));
        // Define table cells for subtotal, tax, and total values
        PdfPCell subtotalCell = new PdfPCell(new Phrase(Double.toString(invoice.getSubtotal())));
        PdfPCell taxCell = new PdfPCell(new Phrase(Double.toString(invoice.getTax())));
        PdfPCell totalCell = new PdfPCell(new Phrase(Double.toString(invoice.getTotal())));
        // Set colspan and alignment for table header cells
        subtotalHeaderCell.setColspan(2);
        taxHeaderCell.setColspan(2);
        totalHeaderCell.setColspan(2);
        subtotalHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        taxHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        // Add table header cells and value cells to the table
        table.addCell(subtotalHeaderCell);
        table.addCell(subtotalCell);
        table.addCell(taxHeaderCell);
        table.addCell(taxCell);
        table.addCell(totalHeaderCell);
        table.addCell(totalCell);

        document.add(table);
        // Create table for signatures and stamp
        PdfPTable signatureTable = new PdfPTable(NUM_COLUMNS_SIGNATURES);
        signatureTable.setWidthPercentage(WIDTH_PERCENTAGE);
        // Add issuer signature cell to table
        PdfPCell issuerSignatureCell = new PdfPCell(new Phrase("Issuer signature: ____________________________"));
        issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(issuerSignatureCell);
        // Add stamp cell to table
        PdfPCell stampCell = new PdfPCell(new Phrase("Stamp: "));
        stampCell.setBorder(Rectangle.NO_BORDER);
        stampCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.addCell(stampCell);
        // Add client signature cell to table
        PdfPCell clientSignatureCell = new PdfPCell(new Phrase("Client signature: ____________________________"));
        clientSignatureCell.setBorder(Rectangle.NO_BORDER);
        clientSignatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        signatureTable.addCell(clientSignatureCell);
        // Add signature table to document
        document.add(signatureTable);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
