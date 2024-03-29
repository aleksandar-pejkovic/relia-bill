package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Caching(evict = {
            @CacheEvict(value = "invoicesByUser", key = "#principal.getName()"),
            @CacheEvict(value = "itemsByUser", key = "#principal.getName()")
    })
    public ItemDto createItem(ItemDto itemDto, Principal principal) {
        Invoice invoice = obtainInvoice(itemDto.getInvoiceId());
        Item item = modelMapper.map(itemDto, Item.class);
        item.setTaxRate(TaxRate.fromRate(itemDto.getTaxRate()));
        item.calculateTax();
        invoice.increaseTotal(item.getTotal());
        item.setInvoice(invoice);
        productService.registerProductSale(item);

        Item savedItem = itemRepository.save(item);
        return convertItemToDto(savedItem);
    }

    @Caching(evict = {
            @CacheEvict(value = "invoicesByUser", key = "#principal.getName()"),
            @CacheEvict(value = "itemsByUser", key = "#principal.getName()")
    })
    public void deleteItem(Long id, Principal principal) {
        Item item = obtainItem(id);
        itemRepository.delete(item);
        productService.discardProductSale(item);

        Invoice invoice = item.getInvoice();
        invoice.decreaseTotal(item.getTotal());
        invoiceRepository.save(invoice);
    }

    public ItemDto loadItemById(Long id) {
        Item item = obtainItem(id);
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

    private Item obtainItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found!"));
    }

    private Invoice obtainInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found!"));
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
