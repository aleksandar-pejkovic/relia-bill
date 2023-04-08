package dev.alpey.reliabill.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.configuration.validation.user.username.Username;
import dev.alpey.reliabill.model.dto.UserDto;
import dev.alpey.reliabill.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public Set<UserDto> searchUsers(@RequestParam String searchTerm) {
        return userService.searchUsers(searchTerm);
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUserAccount(@Valid @RequestBody UserDto userDto) {
        UserDto userResponse = userService.registerUser(userDto);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("#userDto.username == authentication.name or hasAuthority('SCOPE_UPDATE')")
    @PutMapping
    public ResponseEntity<UserDto> updateUserAccount(@Valid @RequestBody UserDto userDto) {
        UserDto userResponse = userService.updateUser(userDto);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @Secured("SCOPE_GRANT_ADMIN")
    @PutMapping("/promote/{username}")
    public ResponseEntity<UserDto> promoteUserToAdmin(@PathVariable @Username String username) {
        UserDto userResponse = userService.grantAdminRoleToUser(username);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("#username == authentication.name or hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/delete/{username}")
    public String removeUserAccount(@PathVariable @Username String username) {
        userService.deleteUser(username);
        return "User '" + username + "' deleted!";
    }

    @GetMapping
    public List<UserDto> fetchAllUsers() {
        return userService.loadAllUsers();
    }

    @GetMapping("/username/{username}")
    public UserDto fetchUsersByUsername(@PathVariable @Username String username) {
        return userService.loadUserByUsername(username);
    }

    @GetMapping("/admins")
    public List<UserDto> fetchAdmins() {
        return userService.loadAdmins();
    }
}
