package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {

    @Min(value = 1, message = "Id должно быть положительным.")
    private Integer id;

    @NotBlank(message = "Имя не может быть пустым.")
    @Size(min = 4, max = 20, message = "Длина имени должна быть от 4 до 20 символов.")
    private final String name;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Поле должно содержать email.")
    private final String email;
}



