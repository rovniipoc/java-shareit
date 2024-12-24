package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

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

        return itemRequests.stream()
                .map(ItemRequestMapper::toDetailedItemRequestOutputDto)
                .toList();
    }

    public ItemRequestOutputDto getOneDetailedById(Long requestId) {
        ItemRequest itemRequest = checkExistItemRequestById(requestId);
        return ItemRequestMapper.toDetailedItemRequestOutputDto(itemRequest);
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
