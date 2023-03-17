package dev.alpey.reliabill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.Item;

@Repository
public interface ItemRepository extends ListCrudRepository<Item, Long> {

    @Query(
            "SELECT i "
                    + "FROM Item i "
                    + "WHERE i.invoice.id = :id"
    )
    List<Item> findByInvoiceId(@Param("id") Long invoiceId);
}
