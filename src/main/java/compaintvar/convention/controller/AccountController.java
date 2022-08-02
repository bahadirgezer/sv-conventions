package compaintvar.convention.controller;

import com.sun.istack.NotNull;
import compaintvar.convention.dto.AccountDTO;
import compaintvar.convention.entity.Account;
import compaintvar.convention.exceptions.ResourceNotFoundException;
import compaintvar.convention.service.AccountService;
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
@RequestMapping("api/account")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountController {
    final AccountService accountService;

    /**
     * A1: Verilen id değerine sahip hesabı döner.
     *
     * @param id            Istenen Account'un id değeri
     * @param commentLimit  Dondurulen yorum sayisina limit, default = 5
     * @return              HTTP 200 | 204
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "5")
                                            Integer commentLimit) {

        AccountDTO accountDTO = accountService.getAccountById(id, commentLimit);
        if (accountDTO == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(accountDTO);
    }

    /**
     * A2: Hesap olusturur.
     *
     * @param A2AccountInDTO    Hesap bilgileri
     * @return                  HTTP 201 | 204 | 417
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @NotNull
                                               AccountDTO A2AccountInDTO) {

        Long savedId = accountService.createAccount(A2AccountInDTO);

        HttpHeaders header = new HttpHeaders(); //adding account ID as a header
        header.set("AccountId", savedId.toString());

        return new ResponseEntity<AccountDTO>(header, HttpStatus.ACCEPTED);
    }

    /**
     * A3: Hesap username'ini ve email'ini gunceller.
     *
     * @param id        Guncellenen Account'un id degeri
     * @param username  Yeni username
     * @param email     Yeni email
     * @return          HTTP 201 | 204 | 417
     */
    @PutMapping
    public ResponseEntity<?> updateAccountUsername(@RequestParam Long id,
                                                   @RequestParam(required = false) String username,
                                                   @RequestParam(required = false) String email) {

        Long savedId = accountService.updateUsernameEmail(id, username, email);

        HttpHeaders header = new HttpHeaders(); //adding account ID as a header
        header.set("AccountId", savedId.toString());

        return new ResponseEntity<AccountDTO>(header, HttpStatus.ACCEPTED);
    }

    /**
     * A4: Hesabi siler
     *
     * @param id    Silinen Account'un id degeri
     * @return      HTTP 202 | 406
     */
    @DeleteMapping
    public HttpStatus deleteAccountById(@RequestParam Long id) {
        return accountService.deleteAccount(id) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE;
    }

    /**
     * A5: Hesaplari sayfalanmis bir sekilde dondurur.
     * Default olarak id ile ascending siralar.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki hesap sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @param commentLimit  Hesabin gösterilecek yorum sayısına limit
     * @return              HTTP 200 | 404
     */
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getPaginatedAccounts(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "") String sortBy,
            @RequestParam(required = false, defaultValue = "false") Boolean descending,
            @RequestParam(required = false, defaultValue = "5") Integer commentLimit) {

        List<AccountDTO> accounts = accountService.getAccountsPaginated(page, size, sortBy, descending, commentLimit);

        return ResponseEntity.ok().body(accounts);
    }
}
