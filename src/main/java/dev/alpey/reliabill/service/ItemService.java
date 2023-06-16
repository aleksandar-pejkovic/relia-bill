package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.invoice.InvoiceNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.item.ItemNotFoundException;
import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.dto.ItemDto;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;
import dev.alpey.reliabill.repository.InvoiceRepository;
import dev.alpey.reliabill.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @CacheEvict(value = "itemsByUser", key = "#principal.getName()")
    public ItemDto createItem(ItemDto itemDto, Principal principal) {
        Invoice invoice = invoiceRepository.findById(itemDto.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        Item item = modelMapper.map(itemDto, Item.class);

        item.setInvoice(invoice);
        calculateTax(itemDto, item);

        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    @CacheEvict(value = "itemsByUser", key = "#principal.getName()")
    public void deleteItem(Long id, Principal principal) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found!"));

        Invoice invoice = item.getInvoice();
        invoice.getItems().remove(item);

        itemRepository.delete(item);

        invoice.setTotal(invoice.getTotal() - item.getTotal());
        invoiceRepository.save(invoice);
    }

    public ItemDto loadItemById(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        Item item = optionalItem.orElseThrow(() -> new NoSuchElementException("Item not found!"));
        return convertItemToDto(item);
    }

    public List<ItemDto> loadAllItemsForInvoice(Long invoiceId) {
        List<Item> items = itemRepository.findByInvoiceId(invoiceId);
        return convertItemsToDtoList(items);
    }

    @Cacheable(value = "itemsByUser", key = "#principal.getName()")
    public List<ItemDto> loadAllItemsForCurrentUser(Principal principal) {
        List<Item> items = itemRepository.findByUsername(principal.getName());
        return convertItemsToDtoList(items);
    }

    private static void calculateTax(ItemDto itemDto, Item item) {
        item.setTaxRate(TaxRate.fromRate(itemDto.getTaxRate()));
        item.calculateTax();
        Invoice invoice = item.getInvoice();
        invoice.setTotal(invoice.getTotal() + item.getTotal());
    }

    private List<ItemDto> convertItemsToDtoList(List<Item> items) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());
    }

    private ItemDto convertItemToDto(Item item) {
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        itemDto.setInvoiceId(item.getInvoice().getId());
        itemDto.setTaxRate(item.getTaxRate().getRate());
        return itemDto;
    }
}
