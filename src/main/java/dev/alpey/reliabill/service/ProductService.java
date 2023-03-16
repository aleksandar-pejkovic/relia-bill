package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.product.ProductNotFoundException;
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

    public List<ProductDto> searchProducts(String searchTerm, Principal principal) {
        return productRepository.searchByName(searchTerm).stream()
                .filter(product -> product.getUsername().equals(principal.getName()))
                .map(this::convertProductToDto)
                .collect(Collectors.toList());
    }

    public ProductDto createProduct(ProductDto productDto, Principal principal) {
        Product product = modelMapper.map(productDto, Product.class);
        product.setUsername(principal.getName());
        return convertProductToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Optional<Product> optionalProduct = productRepository.findById(productDto.getId());
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found!");
        }
        Product storedProduct = optionalProduct.get();
        modelMapper.map(productDto, Product.class);
        return convertProductToDto(productRepository.save(storedProduct));
    }

    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product not found!");
        }
    }

    public List<ProductDto> loadAllProductsForLoggedUser(Principal principal) {
        List<Product> products = productRepository.findByUsername(principal.getName());
        if (products.isEmpty()) {
            throw new ProductNotFoundException("Products not found!");
        }
        return convertProductsToDtoList(products);
    }

    public List<ProductDto> loadAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ProductNotFoundException("Products not found!");
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
