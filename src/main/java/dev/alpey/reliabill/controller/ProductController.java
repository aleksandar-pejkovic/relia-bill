package dev.alpey.reliabill.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.model.dto.ProductDto;
import dev.alpey.reliabill.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public List<ProductDto> searchProducts(@RequestParam String searchTerm) {
        return productService.searchProducts(searchTerm);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto productResponse = productService.createProduct(productDto);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @PreAuthorize("productDto.username == authentication.name or hasAuthority('SCOPE_UPDATE')")
    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto productResponse = productService.updateProduct(productDto);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PreAuthorize("productDto.username == authentication.name or hasAuthority('SCOPE_DELETE')")
    @DeleteMapping
    public String removeProduct(@Valid @RequestBody ProductDto productDto) {
        productService.deleteProduct(productDto.getId());
        return "Product '" + productDto.getName() + "' deleted!";
    }

    @GetMapping
    public List<ProductDto> fetchAllProducts(Principal principal) {
        return productService.loadAllProductsByUsername(principal.getName());
    }
}
