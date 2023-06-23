package dev.alpey.reliabill.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.configuration.exceptions.passwordResetToken.PasswordResetTokenNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.EmailExistsException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameExistsException;
import dev.alpey.reliabill.enums.RoleName;
import dev.alpey.reliabill.model.dto.UserDto;
import dev.alpey.reliabill.model.entity.PasswordResetToken;
import dev.alpey.reliabill.model.entity.Product;
import dev.alpey.reliabill.model.entity.Role;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.CompanyRepository;
import dev.alpey.reliabill.repository.PasswordResetTokenRepository;
import dev.alpey.reliabill.repository.ProductRepository;
import dev.alpey.reliabill.repository.RoleRepository;
import dev.alpey.reliabill.repository.UserRepository;
import dev.alpey.reliabill.service.email.EmailService;

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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Value("${client.url}")
    private String clientUrl;

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
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UsernameExistsException("Account with username '" + userDto.getUsername() + "' already exist");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException("Account with email '" + userDto.getEmail() + "' already exist");
        }
        ZoneId belgradeTimeZone = ZoneId.of("Europe/Belgrade");
        LocalDate currentDate = LocalDate.now(belgradeTimeZone);
        User user = modelMapper.map(userDto, User.class);
        user.setCreationDate(currentDate);
        encryptUserPassword(user);
        assignDefaultRoleToUser(user);
        User registeredUser = userRepository.save(user);
        emailService.sendEmailToAdmin("""
                        Account created
                        """,
                """
                        %s just created an account with a username %s.
                        Creation date %s.
                        """.formatted(
                        registeredUser.getName(),
                        registeredUser.getUsername(),
                        registeredUser.getCreationDate()
                ));
        return convertUserToDto(registeredUser);
    }

    @CachePut(value = "usersByUsername", key = "#userDto.username")
    public UserDto updateUser(UserDto userDto) {
        User currentUser = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (!currentUser.getEmail().equals(userDto.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException("Account with email '" + userDto.getEmail() + "' already exist");
        }
        userDto.setPassword(null);
        modelMapper.map(userDto, currentUser);
        User updatedUser = userRepository.save(currentUser);
        return convertUserToDto(updatedUser);
    }

    public void resetPassword(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        String token = UUID.randomUUID().toString();
        String resetLink = clientUrl + "/reset-password?token=" + token;
        createPasswordResetTokenForUser(user, token);
        emailService.sendEmail(
                user.getEmail(),
                "Password reset token",
                """
                        Hello %s!
                        Please click the following link to reset your password:
                        %s
                        """.formatted(
                        user.getUsername(),
                        resetLink)
        );
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public void updatePassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new PasswordResetTokenNotFoundException("Reset token has already been used!"));

        if (passwordResetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new PasswordResetTokenNotFoundException("Reset token expired!");
        }

        User user = passwordResetToken.getUser();
        encryptUserPassword(user, newPassword);
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public UserDto grantAdminRoleToUser(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        List<Product> usersProducts = productRepository.findByUsername(username);
        if (!usersProducts.isEmpty()) {
            productRepository.deleteAll(usersProducts);
        }
        userRepository.deleteById(user.getId());
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

    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertUserToDto)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found!")
                );
    }

    private void encryptUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void encryptUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private void assignDefaultRoleToUser(User user) {
        Role userRole = roleRepository.findByName(RoleName.USER);
        user.setRoles(Collections.singleton(userRole));
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
