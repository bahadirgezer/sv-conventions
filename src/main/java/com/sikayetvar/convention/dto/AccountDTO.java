package com.sikayetvar.convention.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDTO {
    Long id;
    String email;
    String username;
    Set<Comment> comments = new HashSet<Comment>();
    Long commentCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Comment {
        Long id;
        String content;
        Long ownerId;
        Long previousId;
        Long nextId;
    }
}

