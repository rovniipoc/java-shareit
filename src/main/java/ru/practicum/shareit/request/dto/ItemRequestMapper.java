package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestInputDto itemRequestInputDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestInputDto.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

    public static ItemRequestOutputDto toItemRequestOutputDto(ItemRequest itemRequest) {
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto();
        itemRequestOutputDto.setId(itemRequest.getId());
        itemRequestOutputDto.setDescription(itemRequest.getDescription());
        itemRequestOutputDto.setRequestor(UserMapper.toUserOutputDto(itemRequest.getRequestor()));
        itemRequestOutputDto.setCreated(itemRequest.getCreated());
        return itemRequestOutputDto;
    }

    public static ItemRequestOutputDto toDetailedItemRequestOutputDto(ItemRequest itemRequest) {
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto();
        itemRequestOutputDto.setId(itemRequest.getId());
        itemRequestOutputDto.setDescription(itemRequest.getDescription());
        itemRequestOutputDto.setRequestor(UserMapper.toUserOutputDto(itemRequest.getRequestor()));
        itemRequestOutputDto.setCreated(itemRequest.getCreated());

        List<ItemOutputDto> items = itemRequest.getItems().stream()
                .map(ItemMapper::toItemOutputDto)
                .toList();
        itemRequestOutputDto.setItems(items);

        return itemRequestOutputDto;
    }
}
