package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.validation.CreateGroup;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingInputDtoTests {

//    private static Validator validator;
//
//    @BeforeAll
//    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    void testValidBookingInputDto() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void testStartDateInThePast() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().minusHours(1));
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(1, violations.size());
//
//        boolean foundFutureOrPresentDateViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("должно содержать сегодняшнее число или дату, которая еще не наступила".equals(violation.getMessage())) {
//                foundFutureOrPresentDateViolation = true;
//            }
//        }
//
//        assertTrue(foundFutureOrPresentDateViolation, "Ожидалось нарушение валидации: должно содержать сегодняшнее число или дату, которая еще не наступила");
//    }
//
//    @Test
//    void testEndDateInThePast() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setEnd(LocalDateTime.now().minusHours(1));
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(2, violations.size());
//
//        boolean foundFutureDateViolation = false;
//        boolean foundStartBeforeEndViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("должно содержать дату, которая еще не наступила".equals(violation.getMessage())) {
//                foundFutureDateViolation = true;
//            }
//            if ("Время начала бронирования должно предшествовать времени его завершения".equals(violation.getMessage())) {
//                foundStartBeforeEndViolation = true;
//            }
//        }
//
//        assertTrue(foundFutureDateViolation, "Ожидалось нарушение валидации: должно содержать дату, которая еще не наступила");
//        assertTrue(foundStartBeforeEndViolation, "Ожидалось нарушение валидации: Время начала бронирования должно предшествовать времени его завершения");
//    }
//
//    @Test
//    void testStartDateAfterEndDate() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(2));
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(1, violations.size());
//
//        boolean foundStartBeforeEndViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("Время начала бронирования должно предшествовать времени его завершения".equals(violation.getMessage())) {
//                foundStartBeforeEndViolation = true;
//            }
//        }
//
//        assertTrue(foundStartBeforeEndViolation, "Ожидалось нарушение валидации: Время начала бронирования должно предшествовать времени его завершения");
//    }
//
//    @Test
//    void testNullStartDate() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(null);
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(2, violations.size());
//
//        boolean foundNotNullViolation = false;
//        boolean foundStartBeforeEndViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("не должно равняться null".equals(violation.getMessage())) {
//                foundNotNullViolation = true;
//            }
//            if ("Время начала бронирования должно предшествовать времени его завершения".equals(violation.getMessage())) {
//                foundStartBeforeEndViolation = true;
//            }
//        }
//
//        assertTrue(foundStartBeforeEndViolation, "Ожидалось нарушение валидации: Время начала бронирования должно предшествовать времени его завершения");
//        assertTrue(foundNotNullViolation, "Ожидалось нарушение валидации: не должно равняться null");
//    }
//
//    @Test
//    void testNullEndDate() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setEnd(null);
//        bookingInputDto.setItemId(1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(2, violations.size());
//
//        boolean foundNotNullViolation = false;
//        boolean foundStartBeforeEndViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("не должно равняться null".equals(violation.getMessage())) {
//                foundNotNullViolation = true;
//            }
//            if ("Время начала бронирования должно предшествовать времени его завершения".equals(violation.getMessage())) {
//                foundStartBeforeEndViolation = true;
//            }
//        }
//
//        assertTrue(foundStartBeforeEndViolation, "Ожидалось нарушение валидации: Время начала бронирования должно предшествовать времени его завершения");
//        assertTrue(foundNotNullViolation, "Ожидалось нарушение валидации: не должно равняться null");
//    }
//
//    @Test
//    void testNullItemId() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
//        bookingInputDto.setItemId(0L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(1, violations.size());
//
//        boolean foundPositiveViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("должно быть больше 0".equals(violation.getMessage())) {
//                foundPositiveViolation = true;
//            }
//        }
//
//        assertTrue(foundPositiveViolation, "Ожидалось нарушение валидации: должно быть больше 0");
//    }
//
//    @Test
//    void testNegativeItemId() {
//        BookingInputDto bookingInputDto = new BookingInputDto();
//        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
//        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
//        bookingInputDto.setItemId(-1L);
//
//        Set<ConstraintViolation<BookingInputDto>> violations = validator.validate(bookingInputDto, Default.class, CreateGroup.class);
//        assertEquals(1, violations.size());
//
//        boolean foundPositiveViolation = false;
//
//        for (ConstraintViolation<BookingInputDto> violation : violations) {
//            if ("должно быть больше 0".equals(violation.getMessage())) {
//                foundPositiveViolation = true;
//            }
//        }
//
//        assertTrue(foundPositiveViolation, "Ожидалось нарушение валидации: должно быть больше 0");
//    }
}
