package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Payment;

@Repository
public interface PaymentRepository extends ListCrudRepository<Payment, Long> {

    @Query(
            "SELECT p "
                    + "FROM Payment p "
                    + "WHERE p.invoice.id = :invoiceId"
    )
    List<Payment> findByInvoiceId(@Param("invoiceId") Long invoiceId);
}
