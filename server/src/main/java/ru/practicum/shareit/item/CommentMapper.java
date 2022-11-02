package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .itemId(commentDto.getItemId())
                .authorId(commentDto.getAuthorId())
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItemId())
                .authorId(comment.getAuthorId())
                .authorName(userRepository.findById(comment.getAuthorId()).map(User::getName).orElse(null))
                .created(comment.getCreated())
                .build();
    }
}
