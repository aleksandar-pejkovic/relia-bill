package dev.alpey.reliabill.service.fileUpload;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.entity.Product;
import dev.alpey.reliabill.repository.ProductRepository;

@Service
public class FileUploadService {

    @Autowired
    private ProductRepository productRepository;

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    @Transactional
    public void saveProductsFromFile(byte[] fileData, String filename, Principal principal) {
        try (InputStream inputStream = new ByteArrayInputStream(fileData); Workbook workbook =
                createWorkbook(inputStream, filename)) {
            Sheet sheet = workbook.getSheetAt(0);
            Stream<Row> rowStream = StreamSupport.stream(sheet.spliterator(), false);
            List<Product> products = new ArrayList<>();
            rowStream
                    .skip(1) // Skip the header row
                    .forEach(row -> {
                        Cell pluCell = row.getCell(0);
                        Cell nameCell = row.getCell(1);
                        Cell priceCell = row.getCell(2);

                        int plu = parsePluCellValue(pluCell);
                        String name = parseNameCellValue(nameCell);
                        double price = parsePriceCellValue(priceCell);

                        String productName = name
                                .substring(0, 1)
                                .toUpperCase()
                                + name
                                .substring(1)
                                .toLowerCase()
                                .trim();
                        Product product = Product.builder()
                                .plu(plu)
                                .name(productName)
                                .price(price)
                                .username(principal.getName())
                                .taxRate(TaxRate.RATE_20)
                                .build();

                        productRepository.findByPlu(product.getPlu())
                                .stream()
                                .filter(storedProduct -> storedProduct
                                        .getUsername()
                                        .equals(product.getUsername()))
                                .findFirst()
                                .ifPresent(storedProduct -> product.setId(storedProduct.getId()));
                        products.add(product);
                    });
            productRepository.saveAll(products);
        } catch (IOException | EncryptedDocumentException ignored) {
        }
    }

    private Workbook createWorkbook(InputStream inputStream, String filename) throws IOException {
        if (filename.endsWith(".csv")) {
            return createCSVWorkbook(inputStream);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return WorkbookFactory.create(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private Workbook createCSVWorkbook(InputStream inputStream) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int rowNum = 0;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");

            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
            }
        }

        return workbook;
    }

    private int parsePluCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> Integer.parseInt(cell.getStringCellValue());
            default -> throw new IllegalArgumentException("Invalid PLU value.");
        };
    }

    private String parseNameCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            throw new IllegalArgumentException("Invalid name value.");
        }
    }

    private double parsePriceCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> Double.parseDouble(cell.getStringCellValue());
            default -> throw new IllegalArgumentException("Invalid price value.");
        };
    }
}
