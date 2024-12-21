package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestOutputDto create(ItemRequestInputDto itemRequestInputDto, Long userId) {
        User user = checkExistUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestInputDto, user);
        return ItemRequestMapper.toItemRequestOutputDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestOutputDto> getAll() {
        return itemRequestRepository.findAllByOrderByCreatedDesc().stream()
                .map(ItemRequestMapper::toItemRequestOutputDto)
                .toList();
    }

    public List<ItemRequestOutputDto> getAllDetailedByUser(Long userId) {
        checkExistUserById(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<ItemOutputDto> items = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemOutputDto)
                .toList();
        Map<Long, List<ItemOutputDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toDetailedItemRequestOutputDto(itemRequest, itemsByRequestId.getOrDefault(itemRequest.getId(), List.of())))
                .toList();
    }

    public ItemRequestOutputDto getOneDetailedById(Long requestId) {
        ItemRequest itemRequest = checkExistItemRequestById(requestId);
        List<ItemOutputDto> items = ItemMapper.toItemOutputDto(itemRepository.findByRequestId(requestId));
        return ItemRequestMapper.toDetailedItemRequestOutputDto(itemRequest, items);
    }

    private User checkExistUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
    }

    private ItemRequest checkExistItemRequestById(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + id + " не существует"));
    }
}
