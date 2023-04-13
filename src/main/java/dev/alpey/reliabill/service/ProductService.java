package dev.alpey.reliabill.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ProductDto createProduct(ProductDto productDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Product product = modelMapper.map(productDto, Product.class);
        product.setUsername(authentication.getName());
        product.setTaxRate(TaxRate.fromRate(productDto.getTaxRate()));
        return convertProductToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Optional<Product> optionalProduct = productRepository.findById(productDto.getId());
        Product storedProduct = optionalProduct.orElseThrow(() -> new ProductNotFoundException("Product not found!"));
        modelMapper.map(productDto, storedProduct);
        Product updatedProduct = productRepository.save(storedProduct);
        return convertProductToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product not found!");
        }
    }

    public List<ProductDto> loadAllProductsByUsername(String username) {
        List<Product> products = productRepository.findByUsername(username);
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        return convertProductsToDtoList(products);
    }

    public List<ProductDto> loadAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        return convertProductsToDtoList(products);
    }

    private List<ProductDto> convertProductsToDtoList(List<Product> products) {
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
