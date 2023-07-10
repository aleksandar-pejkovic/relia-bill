package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Product;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, Long> {

    List<Product> findByName(String name);

    List<Product> findByUsername(String username);

    List<Product> searchByName(String name);

    boolean existsByPluAndUsername(Integer plu, String name);
}
