package dev.alpey.reliabill.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.product.ProductNotFoundException;
import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.dto.ProductDto;
import dev.alpey.reliabill.model.entity.Product;
import dev.alpey.reliabill.repository.ProductRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ProductDto> searchProducts(String searchTerm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return productRepository.searchByName(searchTerm).stream()
                .filter(product -> product.getUsername().equals(authentication.getName()))
                .map(this::convertProductToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    public ProductDto createProduct(ProductDto productDto, Principal principal) {
        Product product = modelMapper.map(productDto, Product.class);
        product.setUsername(principal.getName());
        product.setTaxRate(TaxRate.fromRate(productDto.getTaxRate()));
        Product savedProduct = productRepository.save(product);
        return convertProductToDto(savedProduct);
    }

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    public ProductDto updateProduct(ProductDto productDto, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(productDto.getId());
        Product storedProduct = optionalProduct.orElseThrow(() -> new ProductNotFoundException("Product not found!"));
        modelMapper.map(productDto, storedProduct);
        Product updatedProduct = productRepository.save(storedProduct);
        return convertProductToDto(updatedProduct);
    }

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    public void deleteProduct(Long id, Principal principal) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product not found!");
        }
    }

    @Cacheable(value = "productsByUser", key = "#username")
    public List<ProductDto> loadAllProductsByUsername(String username) {
        List<Product> products = productRepository.findByUsername(username);
        return convertProductsToDtoList(products);
    }

    public List<ProductDto> loadAllProducts() {
        List<Product> products = productRepository.findAll();
        return convertProductsToDtoList(products);
    }

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    public List<ProductDto> readProductsFromFile(byte[] fileData, String filename, Principal principal) {
        List<Product> products = new ArrayList<>();
        List<Product> savedProducts;

        try (InputStream inputStream = new ByteArrayInputStream(fileData); Workbook workbook =
                createWorkbook(inputStream, filename)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                int plu;
                String name;
                double price;

                Cell pluCell = row.getCell(0);
                if (pluCell.getCellType() == CellType.NUMERIC) {
                    plu = (int) pluCell.getNumericCellValue();
                } else if (pluCell.getCellType() == CellType.STRING) {
                    plu = Integer.parseInt(pluCell.getStringCellValue());
                } else {
                    // Handle the case when the PLU value is neither numeric nor string
                    throw new IllegalArgumentException("Invalid PLU value.");
                }

                Cell nameCell = row.getCell(1);
                if (nameCell.getCellType() == CellType.STRING) {
                    name = nameCell.getStringCellValue();
                } else {
                    // Handle the case when the name value is not a string
                    throw new IllegalArgumentException("Invalid name value.");
                }

                Cell priceCell = row.getCell(2);
                if (priceCell.getCellType() == CellType.NUMERIC) {
                    price = priceCell.getNumericCellValue();
                } else if (priceCell.getCellType() == CellType.STRING) {
                    price = Double.parseDouble(priceCell.getStringCellValue());
                } else {
                    // Handle the case when the price value is neither numeric nor string
                    throw new IllegalArgumentException("Invalid price value.");
                }

                Product product = new Product();
                product.setPlu(plu);
                String productName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                product.setName(productName);
                product.setPrice(price);
                product.setUsername(principal.getName());
                product.setTaxRate(TaxRate.RATE_20);

                products.add(product);
            }
            savedProducts = productRepository.saveAll(products);
        } catch (IOException | EncryptedDocumentException ex) {
            return new ArrayList<>();
        }
        return convertProductsToDtoList(savedProducts);
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

    private List<ProductDto> convertProductsToDtoList(List<Product> products) {
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        return products.stream()
                .map(this::convertProductToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertProductToDto(Product product) {
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setTaxRate(product.getTaxRate().getRate());
        return dto;
    }
}
