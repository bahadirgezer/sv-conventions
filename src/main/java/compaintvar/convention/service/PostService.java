package compaintvar.convention.service;

import com.sun.istack.NotNull;
import compaintvar.convention.dto.PostDTO;
import compaintvar.convention.entity.Post;
import compaintvar.convention.exception.MsDBOperationException;
import compaintvar.convention.repository.PostRepository;
import compaintvar.convention.request.PostRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static compaintvar.convention.filter.PostSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostService {

    final PostRepository postRepository;

    /**
     * Gonderiler sayfalar ve filtreler.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki hesap sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @param userId        userId filtresi
     * @param title         title benzerlik filtresi
     * @param topicId       topicId filtresi
     * @return              Gonderi listesi
     */
    public List<PostDTO> getPostsPaginatedAndFiltered(Integer page, Integer size, @NotNull String sortBy,
                                                      Boolean descending, Long userId, String title, Long topicId) {

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
        Page<Post> pagedResult;
        try {
            pagedResult =  postRepository.findAll(
                    where(equalsUserId(userId)).and(likeTitle(title)).and(equalsTopicId(topicId)).and(isNotDeleted()),
                    pageable);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get page.");
        }
        if (pagedResult == null) {
            throw new ResourceNotFoundException(
                    String.format("Page request with page = %d, size = %d, sortBy = %s failed.",
                            page, size, sortBy));
        }

        return pagedResult.stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getUserId(),
                        post.getTitle(),
                        post.getBody(),
                        post.getTopicId(),
                        post.getCreateTime(),
                        post.getUpdateTime()
                ))
                .collect(Collectors.toList());
    }

    /**
     * PostRequest'te verilen bilgileri kullanarak yeni gonderi olusturur.
     *
     * @param postRequest   Post bilgileri
     * @return              Olusturulan Post'un id degeri
     */
    public Long createPost(PostRequest postRequest) {

        Post post = new Post(
                null,
                postRequest.getUserId(),
                postRequest.getTitle(),
                postRequest.getBody(),
                postRequest.getTopicId(),
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                false);

        Post savedPost;
        try {
            savedPost = postRepository.save(post);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to create new post");
        }

        return savedPost.getId();
    }

    /**
     * Gonderi title, body ve konusunu gunceler.
     *
     * @param id        Guncellenecek Post'un id degeri
     * @param title     Yeni title
     * @param body      Yeni body
     * @param topicId   Yeni topic_id degeri
     * @return          Post bilgileri
     */
    public PostDTO updatePost(Long id, @NotNull String title, @NotNull String body, Long topicId) {
        Post post;

        //fetching
        try {
            post = postRepository.findPostByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to find post");
        }
        if (post == null) {
            throw new ResourceNotFoundException(
                    "Account with id = " + id + " does not exist");
        }

        //updating
        post.setTitle(title);
        post.setBody(body);

        if (topicId != null)
            post.setTopicId(topicId);

        post.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        //saving
        Post savedPost;
        try {
            savedPost = postRepository.save(post);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save post");
        }

        return new PostDTO(
                savedPost.getId(),
                savedPost.getUserId(),
                savedPost.getTitle(),
                savedPost.getBody(),
                savedPost.getTopicId(),
                savedPost.getCreateTime(),
                savedPost.getUpdateTime()
                );
    }



    /**
     * Gonderiyi siler.
     *
     * @param id Silinecek Post'un id degeri
     * @return
     */
    @Transactional
    public boolean deletePost(Long id) {
        Post post;

        try {
            post = postRepository.findPostByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to find post");
        }
        if (post == null) {
            throw new ResourceNotFoundException(
                    "Post with id = " + id + " does not exist");
        }

        post.setDeleted(true);

        return true;
    }
}
