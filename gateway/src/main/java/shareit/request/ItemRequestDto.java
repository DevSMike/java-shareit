package shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shareit.item.RequestItemDto;
import shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private Collection<RequestItemDto> items;
}
