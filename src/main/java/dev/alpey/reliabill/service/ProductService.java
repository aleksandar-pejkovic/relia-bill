package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Product product = modelMapper.map(productDto, Product.class);
        product.setUsername(authentication.getName());
        product.setTaxRate(TaxRate.fromRate(productDto.getTaxRate()));
        return convertProductToDto(productRepository.save(product));
    }

    @CacheEvict(value = "productsByUser", key = "#principal.getName()")
    public ProductDto updateProduct(ProductDto productDto) {
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
