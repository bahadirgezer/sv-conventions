package compaintvar.convention.dto;

import compaintvar.convention.entity.Account;
import compaintvar.convention.entity.Comment;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {
    Long id;
    String content;
    Account owner;
    Comment previous;
    Comment next;
}
