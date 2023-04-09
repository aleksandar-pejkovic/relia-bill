package dev.alpey.reliabill.service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.company.CompanyNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.model.dto.CompanyDto;
import dev.alpey.reliabill.model.entity.Company;
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

    public List<CompanyDto> searchCompanies(String searchTerm, Principal principal) {
        return companyRepository.searchByName(searchTerm).stream()
                .filter(company -> company.getUser().getUsername().equals(principal.getName()))
                .map(this::convertCompanyToDto)
                .collect(Collectors.toList());
    }

    public CompanyDto createOwnCompany(CompanyDto companyDto, Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        User loggedUser = optionalUser.get();
        Company ownCompany = modelMapper.map(companyDto, Company.class);
        ownCompany.setUser(loggedUser);
        ownCompany = companyRepository.save(ownCompany);
        loggedUser.setCompany(ownCompany);
        Company savedCompany = userRepository.save(loggedUser).getCompany();
        return convertCompanyToDto(savedCompany);
    }

    public CompanyDto createClientCompany(CompanyDto companyDto, Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        User loggedUser = optionalUser.get();
        Company clientCompany = modelMapper.map(companyDto, Company.class);
        clientCompany.setUser(loggedUser);
        Company savedCompany = companyRepository.save(clientCompany);
        return convertCompanyToDto(savedCompany);
    }

    public CompanyDto updateCompany(CompanyDto companyDto) {
        Optional<Company> optionalCompany = companyRepository.findById(companyDto.getId());
        if (optionalCompany.isEmpty()) {
            throw new CompanyNotFoundException("Company not found!");
        }
        Company storedCompany = optionalCompany.get();
        modelMapper.map(companyDto, storedCompany);
        Company updatedCompany = companyRepository.save(storedCompany);
        return convertCompanyToDto(updatedCompany);
    }

    public void deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else {
            throw new CompanyNotFoundException("Company not found!");
        }
    }

    public List<CompanyDto> loadAllCompaniesForLoggedUser(Principal principal) {
        List<Company> companies = companyRepository.findByUsername(principal.getName());
        if (companies.isEmpty()) {
            throw new CompanyNotFoundException("There are no companies stored!");
        }
        return convertCompaniesToDtoList(companies);
    }

    public CompanyDto loadCompanyById(Long id) {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if (optionalCompany.isEmpty()) {
            throw new NoSuchElementException("Company not found!");
        }
        Company company = optionalCompany.get();
        return convertCompanyToDto(company);
    }

    public List<CompanyDto> loadAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            throw new CompanyNotFoundException("There are no companies stored!");
        }
        return convertCompaniesToDtoList(companies);
    }

    private List<CompanyDto> convertCompaniesToDtoList(List<Company> companies) {
        return companies.stream()
                .map(this::convertCompanyToDto)
                .collect(Collectors.toList());
    }

    private CompanyDto convertCompanyToDto(Company company) {
        return modelMapper.map(company, CompanyDto.class);
    }
}
