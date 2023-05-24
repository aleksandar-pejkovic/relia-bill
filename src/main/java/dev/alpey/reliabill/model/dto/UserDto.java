package dev.alpey.reliabill.model.dto;

import java.time.LocalDate;

import dev.alpey.reliabill.configuration.validation.user.name.Name;
import dev.alpey.reliabill.configuration.validation.user.password.Password;
import dev.alpey.reliabill.configuration.validation.user.username.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private Long id;

    @Username
    private String username;

    @Password
    private String password;

    @NotNull
    @Email
    private String email;

    @Name
    private String name;

    private Boolean vatStatus;

    private LocalDate creationDate;
}
