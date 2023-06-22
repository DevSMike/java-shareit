package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.RequestItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestItemDtoTest {

    @Autowired
    private JacksonTester<RequestItemDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        RequestItemDto requestItemDto = RequestItemDto.builder()
                .id(1L)
                .requestId(2L)
                .ownerId(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        JsonContent<RequestItemDto> result = json.write(requestItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

}