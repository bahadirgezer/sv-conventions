package compaintvar.convention.request;

import compaintvar.convention.annotation.ValidBody;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {

    @Size(min = 2, message = "{post-request.title.short}")
    @Size(max = 255, message = "{post-request.title.long}")
    String title;

    @Size(min = 200, message = "{post-request.body.short}")
    @Size(max = 1000, message = "{post-request.body.long}")
    //@Pattern(regexp = "^(?!Asla)[\\s\\S]*\\.\\s*$")
    @ValidBody
    String body;

    Long userId;

    Long topicId;

}
