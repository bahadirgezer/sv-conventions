package compaintvar.convention.controller;

import com.sun.istack.NotNull;
import compaintvar.convention.dto.AccountDTO;
import compaintvar.convention.dto.CommentDTO;
import compaintvar.convention.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/comment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentController {

    final CommentService commentService;

    /**
     * C1: Verilen id değerine sahip yorumu döner.
     *
     * @param id            Istenen Comment'in id değeri
     * @return              HTTP 200 | 204
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {

        CommentDTO commentDTO = commentService.getCommentById(id);
        if (commentDTO == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(commentDTO);
    }

    /**
     * C2: Yorum olusturur.
     *
     * @param C2CommentInDTO    Yorum bilgileri
     * @return                  HTTP 201 | 204 | 417
     */
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody @NotNull
                                           CommentDTO C2CommentInDTO) {

        Long savedId = commentService.createComment(C2CommentInDTO);

        HttpHeaders header = new HttpHeaders(); //adding account ID as a header
        header.set("CommentId", savedId.toString());

        return new ResponseEntity<AccountDTO>(header, HttpStatus.ACCEPTED);
    }

    /**
     * C3: Yorumun sahibini, oncesinde ve sonrasinda gelen yorumu gunceller.
     *
     * @param id            Guncellenen yorumun id degeri
     * @param ownerId       Yeni Owner'in id degeri
     * @param previousId    Yeni bir onceki yorumun id degeri
     * @param nextId        Yeni bir sonraki yorumun id degeri
     * @return              HTTP 201 | 204 | 417
     */
    @PutMapping
    public ResponseEntity<?> updateOwnerPreviousNext(
            @RequestParam Long id,
            @RequestParam(name = "owner", required = false)
            Long ownerId,
            @RequestParam(name = "previous", required = false)
            Long previousId,
            @RequestParam(name = "next", required = false)
            Long nextId) {

        Long savedId = commentService.updateOwnerPreviousNext(id, ownerId, previousId, nextId);

        HttpHeaders header = new HttpHeaders(); //adding account ID as a header
        header.set("CommentId", savedId.toString());

        return new ResponseEntity<AccountDTO>(header, HttpStatus.ACCEPTED);
    }

    /**
     * C4: Comment'i siler.
     *
     * @param id    Silinen Comment'in id degeri
     * @return      HTT{ 202 | 406
     */
    @DeleteMapping
    public HttpStatus deleteCommentById(@RequestParam Long id) {
        return commentService.deleteComment(id) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE;
    }


    /**
     * C5: Yorumlari sayfalanmis bir sekilde dondurur.
     * Default olarak id ile ascending siralar.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki yorum sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @return              HTTP 200 | 404
     */
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getPaginatedAccounts(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "") String sortBy,
            @RequestParam(required = false, defaultValue = "false") Boolean descending) {

        List<CommentDTO> comments = commentService.getPaginatedComments(page, size, sortBy, descending);

        return ResponseEntity.ok().body(comments);
    }
}
