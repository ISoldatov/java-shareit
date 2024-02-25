package ru.practicum.shareit.item.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private final Integer ownerId;

    private Integer requestId;

}
