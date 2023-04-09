package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Company;

@Repository
public interface CompanyRepository extends ListCrudRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE c.user.username = :username")
    List<Company> findByUsername(@Param("username") String username);

    List<Company> searchByName(String companyName);
}
