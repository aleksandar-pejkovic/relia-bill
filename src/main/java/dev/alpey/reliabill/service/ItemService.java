package dev.alpey.reliabill.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(itemDto.getId());
        if (optionalInvoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice not found!");
        }
        Invoice invoice = optionalInvoice.get();
        Item item = modelMapper.map(itemDto, Item.class);
        item.setInvoice(invoice);
        item.setTaxRate(TaxRate.fromRate(itemDto.getTaxRate()));
        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    public ItemDto updateItem(ItemDto itemDto) {
        Optional<Item> optionalItem = itemRepository.findById(itemDto.getId());
        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException("Item not found!");
        }
        Item item = modelMapper.map(itemDto, Item.class);
        item.setTaxRate(TaxRate.fromRate(itemDto.getTaxRate()));
        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    public void deleteItem(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            throw new ItemNotFoundException("Item not found!");
        }
    }

    public ItemDto loadItemById(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            throw new NoSuchElementException("Item not found!");
        }
        Item item = optionalItem.get();
        return convertItemToDto(item);
    }

    public List<ItemDto> loadAllItemsForInvoice(Long invoiceId) {
        List<Item> items = itemRepository.findByInvoiceId(invoiceId);
        if (items.isEmpty()) {
            throw new ItemNotFoundException("There are no items stored for this invoice!");
        }
        return convertItemsToDtoList(items);
    }

    private List<ItemDto> convertItemsToDtoList(List<Item> items) {
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
