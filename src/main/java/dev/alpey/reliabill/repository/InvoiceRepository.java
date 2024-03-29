package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;

@Repository
public interface InvoiceRepository extends ListCrudRepository<Invoice, Long> {

    @Query("SELECT i "
            + "FROM Invoice i "
            + "WHERE i.invoiceStatus <> 'PAID' "
            + "AND i.company.name = :companyName "
            + "ORDER BY i.creationDate DESC")
    List<Invoice> findUnpaidInvoicesByCustomerNameOrderByCreationDateDesc(
            @Param("companyName") String companyName
    );

    @Query("SELECT i "
            + "FROM Invoice i "
            + "WHERE i.invoiceStatus <> 'PAID' "
            + "AND i.company.user.username = :username "
            + "ORDER BY i.creationDate DESC")
    List<Invoice> findUnpaidInvoicesOrderByCreationDateDesc(
            @Param("username") String username
    );

    @Query("SELECT i "
            + "FROM Invoice i "
            + "WHERE i.company.user.username = :username ")
    List<Invoice> findByUsername(@Param("username") String username);

    List<Invoice> findByCompany(Company company);

    List<Invoice> searchByInvoiceNumber(String searchTerm);

    @Query("SELECT i.invoiceNumber "
            + "FROM Invoice i "
            + "WHERE i.id = :id ")
    String findInvoiceNumberById(Long id);
}
