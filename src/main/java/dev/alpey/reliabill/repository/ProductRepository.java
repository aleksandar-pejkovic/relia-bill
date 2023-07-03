package dev.alpey.reliabill.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Product;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByUsername(String username);

    List<Product> findByPlu(Integer plu);

    List<Product> searchByName(String name);
}
