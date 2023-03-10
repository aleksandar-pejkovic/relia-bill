package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.enums.InvoiceStatus;
import dev.alpey.reliabill.model.Invoice;

@Repository
public interface InvoiceRepository extends ListCrudRepository<Invoice, Long> {

    @Query("SELECT i "
            + "FROM Invoice i "
            + "WHERE i.invoiceStatus <> :paidStatus "
            + "AND i.customer.name = :customerName "
            + "ORDER BY i.creationDate DESC")
    List<Invoice> findUnpaidInvoicesByCustomerNameOrderByCreationDateDesc(
            @Param("paidStatus") InvoiceStatus paidStatus,
            @Param("customerName") String customerName
    );
}
