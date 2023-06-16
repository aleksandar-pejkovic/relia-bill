package dev.alpey.reliabill.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.model.dto.ItemDto;
import dev.alpey.reliabill.service.ItemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto, Principal principal) {
        ItemDto createdItem = itemService.createItem(itemDto, principal);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id, Principal principal) {
        itemService.deleteItem(id, principal);
        return "Item deleted!";
    }

    @GetMapping("/{id}")
    public ItemDto fetchItemById(@PathVariable Long id) {
        return itemService.loadItemById(id);
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<ItemDto> fetchAllItemsForInvoice(@PathVariable Long invoiceId) {
        return itemService.loadAllItemsForInvoice(invoiceId);
    }

    @GetMapping
    public List<ItemDto> fetchAllItemsForCurrentUser(Principal principal) {
        return itemService.loadAllItemsForCurrentUser(principal);
    }
}
