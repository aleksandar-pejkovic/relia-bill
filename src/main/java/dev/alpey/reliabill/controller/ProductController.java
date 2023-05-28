package dev.alpey.reliabill.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.alpey.reliabill.model.dto.ProductDto;
import dev.alpey.reliabill.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public List<ProductDto> searchProducts(@RequestParam String searchTerm) {
        return productService.searchProducts(searchTerm);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto, Principal principal) {
        ProductDto productResponse = productService.createProduct(productDto, principal);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto, Principal principal) {
        ProductDto productResponse = productService.updateProduct(productDto, principal);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String removeProduct(@PathVariable Long id, Principal principal) {
        productService.deleteProduct(id, principal);
        return "Product with id '" + id + "' deleted!";
    }

    @GetMapping
    public List<ProductDto> fetchAllProducts(Principal principal) {
        return productService.loadAllProductsByUsername(principal.getName());
    }

    @PostMapping("/upload")
    public List<ProductDto> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No file selected.");
        }
        String filename = file.getOriginalFilename();
        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading file data.", e);
        }
        return productService.readProductsFromFile(fileData, filename, principal);
    }
}
