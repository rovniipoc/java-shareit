package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.validation.CreateGroup;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @Validated(CreateGroup.class) @RequestBody ItemRequestInputDto itemRequestInputDto) {
        log.info("Поступил запрос Post /requests от пользователя с id = {} на добавление ItemRequest с телом {}", userId, itemRequestInputDto);
        ResponseEntity<Object> response = itemRequestClient.create(itemRequestInputDto, userId);
        log.info("Сформирован ответ Post /requests с телом: {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllDetailedByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Поступил запрос Get /requests от пользователя с id = {} на получение List<ItemRequest>", userId);
        ResponseEntity<Object> response = itemRequestClient.getAllDetailedByUser(userId);
        log.info("Сформирован ответ Get /requests с телом: {}", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        log.info("Поступил запрос Get /requests/all на получение List<ItemRequest>");
        ResponseEntity<Object> response = itemRequestClient.getAll();
        log.info("Сформирован ответ Get /requests/all с телом: {}", response);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getDetailedById(@PathVariable Long requestId) {
        log.info("Поступил запрос Get /requests/{} на получение ItemRequest c id = {}", requestId, requestId);
        ResponseEntity<Object> response = itemRequestClient.getOneDetailedById(requestId);
        log.info("Сформирован ответ Get /requests/{} с телом: {}", requestId, response);
        return response;
    }
}
