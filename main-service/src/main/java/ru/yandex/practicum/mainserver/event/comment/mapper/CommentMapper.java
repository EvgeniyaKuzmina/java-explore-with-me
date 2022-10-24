package ru.yandex.practicum.mainserver.event.comment.mapper;

import ru.yandex.practicum.mainserver.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainserver.event.comment.dto.CommentShortDto;
import ru.yandex.practicum.mainserver.event.comment.dto.NewCommentDto;
import ru.yandex.practicum.mainserver.event.comment.dto.UpdateCommentDto;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;
import ru.yandex.practicum.mainserver.event.mapper.EventMapper;
import ru.yandex.practicum.mainserver.user.mapper.UserMapper;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentShortDto toCommentShortDto(Comment comment) {
        CommentShortDto commentDto = CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .build();
        commentDto.setCreat(comment.getCreated());
        return commentDto;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .build();
        commentDto.setCreat(comment.getCreated());
        return commentDto;
    }

    public static Comment fromNewCommentDto(NewCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static Comment fromUpdateCommentDto(UpdateCommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getCommentId())
                .text(commentDto.getText())
                .build();
    }
}
