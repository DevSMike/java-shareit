package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Builder
@Data
public class UserDto {

    Long id;
    final String name;
    @Email(message = "Incorrect email")
    final String email;
}
