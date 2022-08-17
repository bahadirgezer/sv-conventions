package compaintvar.convention.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {

    @Size(min = 2, message = "Title too short.")
    @Size(max = 255, message = "Title too long.")
    String title;

    @Size(min = 200, message = "Body too short.")
    @Size(max = 1000, message = "Body too long.")
    String body;

    Long userId;

    Long topicId;

}
