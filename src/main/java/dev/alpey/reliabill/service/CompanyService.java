package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.company.CompanyNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.enums.CompanyBalanceSortBy;
import dev.alpey.reliabill.model.dto.CompanyDto;
import dev.alpey.reliabill.model.dto.finance.CompanyBalance;
import dev.alpey.reliabill.model.entity.Company;
import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaymentService paymentService;

    public List<CompanyDto> searchCompanies(String searchTerm, Principal principal) {
        return companyRepository.searchByName(searchTerm).stream()
                .filter(company -> company.getUser().getUsername().equals(principal.getName()))
                .map(this::convertCompanyToDto)
                .collect(Collectors.toList());
    }

    @CachePut(value = "ownCompany", key = "#principal.getName()")
    public CompanyDto createOwnCompany(CompanyDto companyDto, Principal principal) {
        User loggedUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        Company ownCompany = modelMapper.map(companyDto, Company.class);
        Company storedCompany = companyRepository.save(ownCompany);
        loggedUser.setCompanyId(storedCompany.getId());
        userRepository.save(loggedUser);
        return convertCompanyToDto(storedCompany);
    }

    @CacheEvict(value = "companiesByUser", key = "#principal.getName()")
    public CompanyDto createClientCompany(CompanyDto companyDto, Principal principal) {
        User loggedUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        Company clientCompany = modelMapper.map(companyDto, Company.class);
        clientCompany.setUser(loggedUser);
        Company savedCompany = companyRepository.save(clientCompany);
        return convertCompanyToDto(savedCompany);
    }

    @Caching(evict = {
            @CacheEvict(value = "ownCompany", key = "#principal.getName()"),
            @CacheEvict(value = "companiesByUser", key = "#principal.getName()")
    })
    public CompanyDto updateCompany(CompanyDto companyDto, Principal principal) {
        Company storedCompany = companyRepository.findById(companyDto.getId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        modelMapper.map(companyDto, storedCompany);
        Company updatedCompany = companyRepository.save(storedCompany);
        return convertCompanyToDto(updatedCompany);
    }

    @Caching(evict = {
            @CacheEvict(value = "companiesByUser", key = "#principal.getName()"),
            @CacheEvict(value = "ownCompany", key = "#principal.getName()")
    })
    public void deleteCompany(Long id, Principal principal) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else {
            throw new CompanyNotFoundException("Company not found!");
        }
    }

    @Cacheable(value = "companiesByUser", key = "#principal.getName()")
    public List<CompanyDto> loadAllCompaniesForLoggedUser(Principal principal) {
        List<Company> companies = companyRepository.findByUsername(principal.getName());
        return convertCompaniesToDtoList(companies);
    }

    @Cacheable(value = "ownCompany", key = "#username")
    public CompanyDto loadOwnCompany(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found!")
                );
        if (user.getCompanyId() == null) {
            throw new CompanyNotFoundException("Company not found!");
        }
        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        return convertCompanyToDto(company);
    }

    public CompanyDto loadCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        return convertCompanyToDto(company);
    }

    public List<CompanyDto> loadAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return convertCompaniesToDtoList(companies);
    }

    public CompanyBalance loadCompanyBalance(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        return loadCompanyBalance(company);
    }

    private CompanyBalance loadCompanyBalance(Company company) {
        double totalRevenue = calculateTotalRevenueForCompany(company);
        double totalPayments = paymentService.calculatePaymentsAmountByCompany(company);
        double totalDebt = totalRevenue - totalPayments;

        return CompanyBalance.builder()
                .name(company.getName())
                .revenue(totalRevenue)
                .payments(totalPayments)
                .debt(totalDebt)
                .build();
    }

    public List<CompanyBalance> sort(List<Company> companies, CompanyBalanceSortBy sortBy) {
        Comparator<CompanyBalance> comparator = switch (sortBy) {
            case REVENUE -> Comparator.comparingDouble(CompanyBalance::getRevenue).reversed();
            case PAYMENTS -> Comparator.comparingDouble(CompanyBalance::getPayments).reversed();
            case DEBT -> Comparator.comparingDouble(CompanyBalance::getDebt).reversed();
            default -> Comparator.comparing(CompanyBalance::getName);
        };

        return companies.stream()
                .map(this::loadCompanyBalance)
                .sorted(comparator)
                .toList();
    }

    private Double calculateTotalRevenueForCompany(Company company) {
        return company
                .getInvoices()
                .stream()
                .mapToDouble(Invoice::getTotal)
                .sum();
    }

    private List<CompanyDto> convertCompaniesToDtoList(List<Company> companies) {
        if (companies.isEmpty()) {
            return new ArrayList<>();
        }
        return companies.stream()
                .map(this::convertCompanyToDto)
                .collect(Collectors.toList());
    }

    private CompanyDto convertCompanyToDto(Company company) {
        return modelMapper.map(company, CompanyDto.class);
    }
}
