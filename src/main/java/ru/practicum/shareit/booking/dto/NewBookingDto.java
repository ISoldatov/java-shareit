package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class NewBookingDto {
    @NotNull
    @Min(value = 1, message = "Id вещи должно быть положительным.")
    Integer itemId;
    @Future
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
}
