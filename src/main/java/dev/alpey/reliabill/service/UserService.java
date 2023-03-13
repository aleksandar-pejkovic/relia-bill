package dev.alpey.reliabill.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameExistsException;
import dev.alpey.reliabill.configuration.exceptions.user.UsernameNotFoundException;
import dev.alpey.reliabill.dto.UserDto;
import dev.alpey.reliabill.enums.RoleName;
import dev.alpey.reliabill.model.Role;
import dev.alpey.reliabill.model.User;
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

    public UserDto registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UsernameExistsException("Account with username '" + userDto.getUsername() + "' already exist");
        }
        userDto.setCreationDate(LocalDate.now());
        User user = convertUserToEntity(userDto);
        encryptUserPassword(user);
        assignDefaultRoleToUser(user);
        User registeredUser = userRepository.save(user);
        return convertUserToDto(registeredUser);
    }

    public UserDto updateUser(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        User currentUser = optionalUser.get();
        userDto.setPassword(null);
        modelMapper.map(userDto, currentUser);
        User updatedUser = userRepository.save(currentUser);
        return convertUserToDto(updatedUser);
    }

    public UserDto grantAdminRoleToUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        User currentUser = optionalUser.get();
        currentUser.getRoles().add(roleRepository.findByName(RoleName.ADMIN));
        User adminUser = userRepository.save(currentUser);
        return convertUserToDto(adminUser);
    }

    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        User currentUser = optionalUser.get();
        userRepository.delete(currentUser);
    }

    public List<UserDto> loadAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new UserNotFoundException("There are no users in the database!");
        }
        return convertUsersToDtoList(users);
    }

    public List<UserDto> loadAdmins() {
        List<User> users = userRepository.findAdmins();
        if (users.isEmpty()) {
            throw new UserNotFoundException("There are no admins in the database!");
        }
        return convertUsersToDtoList(users);
    }

    public UserDto loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertUserToDto)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("Username '%s' not found!", username))
                );
    }

    private void encryptUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private User encryptUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    private void assignDefaultRoleToUser(User user) {
        Role userRole = roleRepository.findByName(RoleName.USER);
        user.setRoles(Collections.singleton(userRole));
    }

    private List<UserDto> convertUsersToDtoList(List<User> users) {
        return users
                .stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertUserToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertUserToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
