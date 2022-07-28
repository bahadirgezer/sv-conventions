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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AccountService accountService;

    /**
     * A1: ID'si parametre olarak verilen Account'u buluyor.
     *
     * @param id Istenen Account'un ID'si
     * @return Istenen Account'un DTO'su
     */
    @GetMapping
    public ResponseEntity<AccountDTO> getAccountById(@RequestParam Long id,
            @RequestParam(defaultValue = "5") Integer commentLimit) {
        AccountDTO accountDTO = accountService.getAccountById(id, commentLimit);
        if (accountDTO == null) {
            log.debug(String.format("Could not get account with ID: %d.", id));
            throw new ResourceNotFoundException(
                    String.format("Account with ID: %d not found.", id));
        }

        return ResponseEntity.ok().body(accountDTO);
    }

    /**
     * A2: Request body olarak gelen Account DTO'sunu veritabanina kaydeder.
     * Eger olusturma islemi basarili olursa kaydedilen Account DTO'sunu
     * HTTP 201 created status ile doner.
     *
     * @param newAccountDTO olusturulmak istenen Account DTO'su
     * @return kaydedilen Account DTO'su
     */
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @NotNull AccountDTO newAccountDTO) {
        AccountDTO accountDTO = accountService.createAccount(newAccountDTO);
        if (accountDTO == null) {
            log.debug(String.format(
                    "Could not create new account with ID: %d.", newAccountDTO.getId()));
            throw new ResourceNotFoundException(
                    String.format("Account with ID: %d not found.", newAccountDTO.getId()));
        }
        return new ResponseEntity<AccountDTO>(accountDTO, HttpStatus.CREATED);
    }

    /**
     * A3: ID'si verilen Account'un username'ini gunceller.
     * Yeni username'in biricik olup olmadigi kontrol edilir.
     *
     * @param id guncellenmek istenen Account'un ID'si
     * @param username yeni username
     * @return Guncellenen Account'un kaydedilen DTO'su
     */
    @PutMapping
    public ResponseEntity<AccountDTO> updateAccountUsername(@RequestParam Long id,
                                                            @RequestParam String username) {
        AccountDTO accountDTO = accountService.updateUsername(id, username);
        if (accountDTO == null) {
            log.debug(String.format(
                    "Could not update username of the account with ID: %d.", id));
            throw new ResourceNotFoundException(
                    String.format("Account with ID: %d not found.", id));
        }
        return new ResponseEntity<AccountDTO>(accountDTO, HttpStatus.ACCEPTED);
    }

    /**
     * A4: ID'si verilen Account'un email'ini gunceller.
     * Yeni email'in biricik olup olmadigi kontrol edilir.
     *
     * @param id guncellenmek istenen Account'un ID'si
     * @param email yeni email
     * @return Guncellenen Account'un kaydedilen DTO'su
     */
    @PutMapping
    public ResponseEntity<AccountDTO> updateAccountEmail(@RequestParam Long id,
                                                            @RequestParam String email) {
        AccountDTO accountDTO = accountService.updateEmail(id, email);
        if (accountDTO == null) {
            log.debug(String.format(
                    "Could not update email of the account with ID: %d.", id));
            throw new ResourceNotFoundException(
                    String.format("Account with ID: %d not found.", id));
        }
        return new ResponseEntity<AccountDTO>(accountDTO, HttpStatus.ACCEPTED);
    }

    /**
     * A5: ID'si verilen Account'u database'den siler.
     *
     * @param id silinmek istenen Account'un ID'si
     * @return islem basarisina gore HTTP status
     */
    @DeleteMapping
    public HttpStatus deleteAccountById(@RequestParam Long id) {
        return accountService.deleteAccount(id) ? HttpStatus.ACCEPTED : HttpStatus.NOT_ACCEPTABLE;
    }

}
