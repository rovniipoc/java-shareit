package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInputDtoTests {

    @Autowired
    private JacksonTester<BookingInputDto> json;

    private static final String START = "2000-01-01T11:11:11";
    private static final String END = "2111-01-01T11:11:11";

    private BookingInputDto bookingInputDto = null;

    @BeforeEach
    public void setup() {
        bookingInputDto = new BookingInputDto();
        bookingInputDto.setStart(LocalDateTime.parse(START));
        bookingInputDto.setEnd(LocalDateTime.parse(END));
    }

    @Test
    public void startSerializes() throws IOException {
        assertThat(json.write(bookingInputDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(START);
    }

    @Test
    public void endSerializes() throws IOException {
        assertThat(json.write(bookingInputDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(END);
    }
}