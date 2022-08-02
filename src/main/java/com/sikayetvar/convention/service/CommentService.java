package com.sikayetvar.convention.service;

import com.sikayetvar.convention.dto.CommentDTO;
import com.sikayetvar.convention.entity.Comment;
import com.sikayetvar.convention.exceptions.MsDBOperationException;
import com.sikayetvar.convention.exceptions.ResourceNotFoundException;
import com.sikayetvar.convention.repository.CommentRepository;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentService {

    final CommentRepository commentRepository;

    /**
     * Verilen id değerine sahip yorumu doner.
     *
     * @param id    Yorumun id degeri
     * @return      Yorum bilgileri
     */
    public CommentDTO getCommentById(Long id) {
        Comment comment;

        try {
            comment = commentRepository.findCommentByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get comment");
        }

        if (comment == null) {
            throw new ResourceNotFoundException(
                    "Comment with id = " + id + " does not exist");
        }

        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getOwner(),
                comment.getPrevious(),
                comment.getNext()
        );
    }


    /**
     * Verilen CommentDTO yorum bilgilerini kayit eder.
     *
     * @param commentDTO    Olusturulmak istenen Comment
     * @return              Kaydedilen Comment'in id degeri
     */
    public Long createComment(@NotNull CommentDTO commentDTO) {
        Comment comment =
                new Comment(
                        commentDTO.getId(),
                        commentDTO.getContent(),
                        commentDTO.getOwner(),
                        commentDTO.getPrevious(),
                        commentDTO.getNext(),
                        false);

        if (comment.getContent().isBlank()) {
            throw new IllegalArgumentException(
                    "Comment content is blank.");
        }

        Comment savedComment;
        try {
            savedComment = commentRepository.save(comment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save comment");
        }

        return savedComment.getId();
    }

    /**
     * Yorum username'ini gunceller.
     *
     * @param id            Guncellenen Comment'in id degeri
     * @param ownerId       Yeni yorum sahibi
     * @param previousId    Yeni onceki yorum
     * @param nextId        Yeni sonraki yorum
     * @return              Guncellenen Comment'in id degeri
     */
    public Long updateOwnerPreviousNext(Long id, Long ownerId, Long previousId, Long nextId) {
        Comment comment;

        try {
            comment = commentRepository.findCommentByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get comment.");
        }
        if (comment == null) {
            throw new ResourceNotFoundException(
                    "Comment with id = " + id + " does not exist");
        }



        Comment savedComment;
        try {
            savedComment = commentRepository.save(comment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save account");
        }

        return savedComment.getId();
    }


    /**
     * Yorumu siler. (soft-delete)
     *
     * @param id    Silinecek Comment'in id degeri
     * @return
     */
    public Boolean deleteComment(Long id) {
        Comment comment;

        try {
            comment = commentRepository.findCommentByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get comment");
        }
        if (comment == null) {
            throw new ResourceNotFoundException(
                    "Comment with id = " + id + " does not exist");
        }

        comment.setDeleted(true);

        Comment savedComment;
        try {
            savedComment = commentRepository.save(comment);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save comment");
        }

        return true;
    }


    /**
     * Yorumlari sayfalandirilmis sekilde doner.
     * Default olarak id ile ascending siralar.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki hesap sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @return              Istenen sayfadaki Comment'lerin listesi
     */
    public List<CommentDTO> getPaginatedComments(Integer page,
                                                 Integer size,
                                                 String sortBy,
                                                 Boolean descending) {
        Sort sort = null;
        if (!sortBy.isBlank()) {
            if (descending) {
                sort = Sort.by(sortBy).descending();
            } else {
                sort = Sort.by(sortBy).ascending();
            }
        }
        if (sort == null) {
            sort = Sort.by("id");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> pagedResult;
        try {
            pagedResult =  commentRepository.findAll(pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get page.");
        }
        if (pagedResult == null) {
            throw new ResourceNotFoundException(
                    String.format("Page request with page = %d, size = %d, sortBy = %s failed.",
                            page, size, sortBy));
        }

        if (pagedResult.hasContent()) {
            return pagedResult.getContent().stream()
                    .map(comment -> new CommentDTO(
                            comment.getId(),
                            comment.getContent(),
                            comment.getOwner(),
                            comment.getPrevious(),
                            comment.getNext())
                    ).collect(Collectors.toList());
        }
        return new ArrayList<CommentDTO>();
    }
}
