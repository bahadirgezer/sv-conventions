package compaintvar.convention.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDTO {
    Long id;
    Long userId;
    String title;
    String body;
    Long topicId;
    Timestamp createTime;
    Timestamp updateTime;
}
