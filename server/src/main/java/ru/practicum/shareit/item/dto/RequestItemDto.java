package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemDto {

    Long id;
    String name;
    Long ownerId;
    String description;
    Boolean available;
    Long requestId;
}
