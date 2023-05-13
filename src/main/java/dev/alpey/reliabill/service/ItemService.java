package dev.alpey.reliabill.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public ItemDto createItem(ItemDto itemDto) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(itemDto.getInvoiceId());
        Invoice invoice = optionalInvoice.orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
        Item item = modelMapper.map(itemDto, Item.class);

        item.setInvoice(invoice);
        calculateTax(itemDto, item);

        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    public ItemDto updateItem(ItemDto itemDto) {
        Optional<Item> optionalItem = itemRepository.findById(itemDto.getId());
        Item item = optionalItem.orElseThrow(() -> new ItemNotFoundException("Item not found!"));
        modelMapper.map(itemDto, item);

        calculateTax(itemDto, item);

        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    public void deleteItem(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        Item item = optionalItem.orElseThrow(() -> new ItemNotFoundException("Item not found!"));

        Invoice invoice = item.getInvoice();
        invoice.getItems().remove(item);

        itemRepository.delete(item);

        invoice.calculateTax();
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

    public List<ItemDto> loadAllItemsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Item> items = itemRepository.findByUsername(username);
        return convertItemsToDtoList(items);
    }

    private static void calculateTax(ItemDto itemDto, Item item) {
        item.setTaxRate(TaxRate.fromRate(itemDto.getTaxRate()));
        item.calculateTax();
        Invoice invoice = item.getInvoice();
        invoice.getItems().add(item);
        invoice.calculateTax();
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
