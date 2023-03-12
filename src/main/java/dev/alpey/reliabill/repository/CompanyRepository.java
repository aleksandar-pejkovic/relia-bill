package dev.alpey.reliabill.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.Company;

@Repository
public interface CompanyRepository extends ListCrudRepository<Company, Long> {
}
