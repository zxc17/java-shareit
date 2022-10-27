package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(ItemDto itemDto, Long ownerId) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Long itemId, Long ownerId) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> getById(Long itemId, Long requesterId) {
        return get("/" + itemId, requesterId);
    }

    public ResponseEntity<Object> getListByOwner(Long ownerId, Long from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> findItems(Long requesterId, String text, Long from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", requesterId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto commentDto, Long itemId, Long userId) {
        final String path = String.format("/%s/comment", itemId);
        return post(path, userId, commentDto);
    }
}
