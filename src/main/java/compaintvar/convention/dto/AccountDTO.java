package compaintvar.convention.dto;

import compaintvar.convention.entity.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    Long id;
    String email;
    String username;
    Set<Comment> comments = new HashSet<Comment>();
    Long commentCount;
}
