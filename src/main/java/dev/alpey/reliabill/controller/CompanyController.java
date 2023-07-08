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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.model.dto.CompanyDto;
import dev.alpey.reliabill.model.dto.finance.CompanyBalance;
import dev.alpey.reliabill.service.CompanyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/{id}")
    public CompanyDto getCompanyById(@PathVariable Long id) {
        return companyService.loadCompanyById(id);
    }

    @GetMapping
    public List<CompanyDto> getAllCompaniesForLoggedUser(Principal principal) {
        return companyService.loadAllCompaniesForCurrentUser(principal);
    }

    @PostMapping("/own")
    public ResponseEntity<CompanyDto> createOwnCompany(
            @Valid @RequestBody CompanyDto companyDto, Principal principal) {
        CompanyDto savedCompanyDto = companyService.createOwnCompany(companyDto, principal);
        return new ResponseEntity<>(savedCompanyDto, HttpStatus.CREATED);
    }

    @PostMapping("/client")
    public ResponseEntity<CompanyDto> createClientCompany(
            @Valid @RequestBody CompanyDto companyDto, Principal principal) {
        CompanyDto savedCompanyDto = companyService.createClientCompany(companyDto, principal);
        return new ResponseEntity<>(savedCompanyDto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CompanyDto> updateCompany(@Valid @RequestBody CompanyDto companyDto, Principal principal) {
        CompanyDto updatedCompanyDto = companyService.updateCompany(companyDto, principal);
        return new ResponseEntity<>(updatedCompanyDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteCompany(@PathVariable Long id, Principal principal) {
        companyService.deleteCompany(id, principal);
        return "Company deleted!";
    }

    @GetMapping("/search")
    public List<CompanyDto> searchCompanies(@RequestParam String searchTerm, Principal principal) {
        return companyService.searchCompanies(searchTerm, principal);
    }

    @GetMapping("/own")
    public CompanyDto fetchOwnCompany(Principal principal) {
        return companyService.loadOwnCompany(principal.getName());
    }

    @GetMapping("/all")
    public List<CompanyDto> getAllCompanies() {
        return companyService.loadAllCompanies();
    }

    @GetMapping("/{id}/balance")
    public CompanyBalance fetchCompanyBalance(@PathVariable Long id) {
        return companyService.loadCompanyBalance(id);
    }
}
