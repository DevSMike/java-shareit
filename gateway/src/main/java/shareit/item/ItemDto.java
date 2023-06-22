package shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shareit.booking.BookingLiteDto;
import shareit.request.ItemRequestDto;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<ItemRequestDto> requests;
    private BookingLiteDto nextBooking;
    private BookingLiteDto lastBooking;
    private Long ownerId;
    private List<CommentDto> comments;
    private Long requestId;
}
