package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.User;

@Repository
public interface CompanyRepository extends ListCrudRepository<Company, Long> {

    List<Company> findByUser(User user);

    @Query("SELECT c FROM Company c WHERE c.user.username = :username")
    List<Company> findByUsername(String username);

    List<Company> searchByName(String companyName);
}
