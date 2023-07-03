package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginDataService {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ItemService itemService;

    @Transactional(readOnly = true)
    public Map<String, Object> loadUsersData(Principal principal) {
        String username = principal.getName();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("user", userService.loadUserByUsername(username));
        dataMap.put("ownCompany", companyService.loadOwnCompany(username));
        dataMap.put("companies", companyService.loadAllCompaniesForLoggedUser(principal));
        dataMap.put("invoices", invoiceService.loadAllInvoicesForLoggedUser(principal));
        dataMap.put("products", productService.loadAllProductsByUsername(username));
        dataMap.put("items", itemService.loadAllItemsForCurrentUser(principal));

        return dataMap;
    }
}
