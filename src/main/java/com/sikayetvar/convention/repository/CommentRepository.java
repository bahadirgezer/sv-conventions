package com.sikayetvar.convention.repository;

import com.sikayetvar.convention.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findCommentByIdAndDeletedFalse(Long id);
    //merge request
}
