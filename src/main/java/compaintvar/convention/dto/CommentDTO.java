package compaintvar.convention.dto;

import compaintvar.convention.entity.Account;
import compaintvar.convention.entity.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {
    Long id;
    String content;
    Account owner;
    Comment previous;
    Comment next;
}
