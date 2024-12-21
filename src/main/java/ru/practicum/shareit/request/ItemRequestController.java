package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.validation.CreateGroup;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestOutputDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @Validated(CreateGroup.class) @RequestBody ItemRequestInputDto itemRequestInputDto) {
        log.info("Поступил запрос Post /requests от пользователя с id = {} на добавление ItemRequest с телом {}", userId, itemRequestInputDto);
        ItemRequestOutputDto newItemRequest = itemRequestService.create(itemRequestInputDto, userId);
        log.info("Сформирован ответ Post /requests с телом: {}", newItemRequest);
        return newItemRequest;
    }

    @GetMapping
    public List<ItemRequestOutputDto> getAllDetailedByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Поступил запрос Get /requests от пользователя с id = {} на получение List<ItemRequest>", userId);
        List<ItemRequestOutputDto> requests = itemRequestService.getAllDetailedByUser(userId);
        log.info("Сформирован ответ Get /requests с телом: {}", requests);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getAll() {
        log.info("Поступил запрос Get /requests/all на получение List<ItemRequest>");
        List<ItemRequestOutputDto> requests = itemRequestService.getAll();
        log.info("Сформирован ответ Get /requests/all с телом: {}", requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutputDto getDetailedById(@PathVariable Long requestId) {
        log.info("Поступил запрос Get /requests/{} на получение ItemRequest c id = {}", requestId, requestId);
        ItemRequestOutputDto request = itemRequestService.getOneDetailedById(requestId);
        log.info("Сформирован ответ Get /requests/{} с телом: {}", requestId, request);
        return request;
    }
}
