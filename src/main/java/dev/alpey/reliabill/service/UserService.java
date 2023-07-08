package dev.alpey.reliabill.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.configuration.exceptions.user.EmailExistsException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameExistsException;
import dev.alpey.reliabill.enums.RoleName;
import dev.alpey.reliabill.model.dto.UserDto;
import dev.alpey.reliabill.model.entity.Product;
import dev.alpey.reliabill.model.entity.Role;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.ProductRepository;
import dev.alpey.reliabill.repository.RoleRepository;
import dev.alpey.reliabill.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Set<UserDto> searchUsers(String searchTerm) {
        Set<User> results = new HashSet<>();
        results.addAll(userRepository.searchByUsername(searchTerm));
        results.addAll(userRepository.searchByName(searchTerm));
        results.addAll(userRepository.searchByEmail(searchTerm));
        return results
                .stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toSet());
    }

    @CachePut(value = "usersByUsername", key = "#userDto.username")
    public UserDto registerUser(UserDto userDto) {
        validateUsername(userDto.getUsername());
        validateEmail(userDto.getEmail());
        User user = modelMapper.map(userDto, User.class);
        setCreationDate(user);
        encryptUserPassword(user);
        assignDefaultRoleToUser(user);
        User registeredUser = userRepository.save(user);
        return convertUserToDto(registeredUser);
    }

    @CachePut(value = "usersByUsername", key = "#userDto.username")
    public UserDto updateUser(UserDto userDto) {
        User currentUser = obtainCurrentUser(userDto.getUsername());
        if (!currentUser.getEmail().equals(userDto.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException("Account with email '" + userDto.getEmail() + "' already exist");
        }
        userDto.setPassword(null);
        modelMapper.map(userDto, currentUser);
        User updatedUser = userRepository.save(currentUser);
        return convertUserToDto(updatedUser);
    }

    public UserDto grantAdminRoleToUser(String username) {
        User currentUser = obtainCurrentUser(username);
        currentUser.getRoles().add(roleRepository.findByName(RoleName.ADMIN));
        User adminUser = userRepository.save(currentUser);
        return convertUserToDto(adminUser);
    }

    @Caching(evict = {
            @CacheEvict(value = "companiesByUser", key = "#username"),
            @CacheEvict(value = "ownCompany", key = "#username"),
            @CacheEvict(value = "usersByUsername", key = "#username"),
            @CacheEvict(value = "productsByUser", key = "#username")})
    public void deleteUser(String username) {
        User currentUser = obtainCurrentUser(username);
        List<Product> usersProducts = productRepository.findByUsername(username);
        if (!usersProducts.isEmpty()) {
            productRepository.deleteAll(usersProducts);
        }
        userRepository.deleteById(currentUser.getId());
    }

    public List<UserDto> loadAllUsers() {
        List<User> users = userRepository.findAll();
        return convertUsersToDtoList(users);
    }

    public List<UserDto> loadAdmins() {
        List<User> users = userRepository.findAdmins();
        return convertUsersToDtoList(users);
    }

    @Cacheable(value = "usersByUsername", key = "#username")
    public UserDto loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertUserToDto)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found!")
                );
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameExistsException("Account with username '" + username + "' already exist");
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailExistsException("Account with email '" + email + "' already exist");
        }
    }

    private void setCreationDate(User user) {
        ZoneId belgradeTimeZone = ZoneId.of("Europe/Belgrade");
        LocalDate currentDate = LocalDate.now(belgradeTimeZone);
        user.setCreationDate(currentDate);
    }

    private void encryptUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void assignDefaultRoleToUser(User user) {
        Role userRole = roleRepository.findByName(RoleName.USER);
        user.setRoles(Collections.singleton(userRole));
    }

    private User obtainCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    private List<UserDto> convertUsersToDtoList(List<User> users) {
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return users
                .stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
