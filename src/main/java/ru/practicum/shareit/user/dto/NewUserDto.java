package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class NewUserDto {
    @NotBlank(message = "Имя не может быть пустым.")
    @Size(min = 4, max = 20, message = "Длина имени должна быть от 4 до 20 символов.")
    private final String name;

    @NonNull
    @Email(message = "Поле должно содержать email.")
    private final String email;
}
