package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Builder
@Data
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Incorrect email")
    private String email;
}
