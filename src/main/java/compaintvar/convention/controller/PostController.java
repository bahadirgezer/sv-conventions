package compaintvar.convention.controller;

import compaintvar.convention.dto.PostDTO;
import compaintvar.convention.request.PostRequest;
import compaintvar.convention.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostController {

    final PostService postService;

    /**
     * P1: Gonderileri sayfalanmis ve filtrelenmis bir sekilde dondurur.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki hesap sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @param userId        userId filtresi
     * @param title         title benzerlik filtresi
     * @param topicId       topicId filtresi
     * @return              HTTP 200 | 204
     */
    @GetMapping
    public ResponseEntity<List<PostDTO>> getPostsPaginatedAndFiltered (
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "") String sortBy,
            @RequestParam(required = false, defaultValue = "false") Boolean descending,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "") @Size(min = 2, max = 255) String title,
            @RequestParam(required = false) Long topicId) {

        List<PostDTO> accounts = postService
                .getPostsPaginatedAndFiltered(page, size, sortBy, descending, userId, title, topicId);

        if (accounts.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(accounts);
    }

    /**
     * P2: Kullanicilar adina icerik olusturma islemini gerceklestirir.
     *
     * @param postRequest   Post bilgileri
     * @return HTTP 201
     */
    @PostMapping
    public ResponseEntity<Long> createPost(
            @Valid @RequestBody PostRequest postRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("PostId", postService.createPost(postRequest).toString())
                .build();
    }

    /**
     * P3: Kullanici bilgilerini gunceller.
     *
     * @param id        Guncellenecek Post'un id degeri
     * @param title     Yeni title
     * @param body      Yeni body
     * @param topicId   Yeni topic_id degeri
     * @return          HTTP 201 | 204 TODO: figure out HTTP return statuses
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestParam  @Size(min = 2, max = 255) String title,
                                        @RequestParam  @Size(min = 200, max = 1000) String body,
                                        @RequestParam Long topicId) {

        PostDTO postDTO = postService.updatePost(id, title, body, topicId);
        if (postDTO == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(postDTO);
    }

    /**
     * P4: Kullaniciyi siler
     *
     * @param id    Silinen Account'un id degeri
     * @return      HTTP 202 | 406
     */
    @DeleteMapping("/{id}")
    public HttpStatus deletePost(@PathVariable Long id) {
        return postService.deletePost(id) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE;
    }
}
