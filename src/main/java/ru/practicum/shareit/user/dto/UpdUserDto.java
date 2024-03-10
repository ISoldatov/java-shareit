package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class UpdUserDto {

    @Size(min = 4, max = 20, message = "Длина имени должна быть от 4 до 20 символов.")
    private String name;

    @Email(message = "Поле должно содержать email.")
    private String email;
}
