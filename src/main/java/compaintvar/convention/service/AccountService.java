package compaintvar.convention.service;

import com.sun.istack.NotNull;
import compaintvar.convention.dto.AccountDTO;
import compaintvar.convention.entity.Account;
import compaintvar.convention.exceptions.DuplicateEntityError;
import compaintvar.convention.exceptions.ResourceNotFoundException;
import compaintvar.convention.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;

    /**
     * Parametre olarak verilen ID'ye gore databaseden Account objesi cekiyor.
     * Bu obje DTO olarak donduruluyor.
     *
     * Default olarak maximum 5 tane comment donduruyor.
     *
     * @param id Istenen Account objesinin ID'si
     * @return Istenen Account objesinin DTO'su
     */
    public AccountDTO getAccountById(Long id) {
        Account account;

        log.debug(String.format("Fetching account with ID: %d.", id));
        try {
            account = accountRepository.findAccountById(id);
        } catch (Exception e) {
            log.debug(String.format("Database query exception: Account fetch with ID: %d failed.", id));
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            log.debug(String.format("Account with ID: %d not found.", id));
            throw new ResourceNotFoundException(String.format("Account (ID: %d) not found.", id));
        }

        return new AccountDTO(account.getId(),
                account.getEmail(),
                account.getUsername(),
                account.getComments() // Butun commentleri dondurmek istemiyoruz
                        .stream().limit(5) // 5 tane
                        .collect(Collectors.toSet()),
                account.getCommentCount());
    }

    /**
     * Parametre olarak verilen ID'ye gore databaseden Account objesi cekiyor.
     * Bu obje DTO olarak donduruluyor.
     *
     * Comment limiti belirlenebiliyor.
     *
     * @param id Istenen Account objesinin ID'si
     * @return Istenen Account objesinin DTO'su
     */
    public AccountDTO getAccountById(Long id, Integer commentLimit) {
        Account account;

        log.debug(String.format("Fetching account with ID: %d.", id));
        try {
            account = accountRepository.findAccountById(id);
        } catch (Exception e) {
            log.debug(String.format("Database query exception: Account fetch with ID: %d failed.", id));
            e.printStackTrace();
            return null;
        }

        if (account == null) {
            log.debug(String.format("Account with ID: %d not found.", id));
            throw new ResourceNotFoundException(String.format("Account (ID: %d) not found.", id));
        }

        return new AccountDTO(account.getId(),
                account.getEmail(),
                account.getUsername(),
                account.getComments() // Butun commentleri dondurmek istemiyoruz
                        .stream().limit(commentLimit)
                        .collect(Collectors.toSet()),
                account.getCommentCount());
    }

    public AccountDTO createAccount(@NotNull AccountDTO accountDTO) {
        log.debug(String.format("Creating new account (ID: %d, username: %s, email: %s).",
                accountDTO.getId(), accountDTO.getUsername(), accountDTO.getEmail()));
        Account account =
                new Account(accountDTO.getId(),
                        accountDTO.getEmail(),
                        accountDTO.getUsername(),
                        accountDTO.getComments(),
                        accountDTO.getCommentCount());

        if (!checkAccountUniqueness(account)) { //if account is not unique
            return null;
        }

        Account savedAccount;
        try {
            savedAccount = accountRepository.save(account);
        } catch (Exception e) {
            log.debug(String.format(
                    "Database query exception: Account save with ID: %d failed.", account.getId()));
            e.printStackTrace();
            return null;
        }

        return new AccountDTO(savedAccount.getId(),
                savedAccount.getEmail(),
                savedAccount.getUsername(),
                savedAccount.getComments(),
                savedAccount.getCommentCount());
    }

    private Boolean checkAccountUniqueness(Account account) {
        Account emailCheck;
        try {
            emailCheck = accountRepository.findAccountByEmail(account.getEmail());
        } catch (Exception e) {
            log.debug(String.format(
                    "Database query exception: Account fetch with email: %s failed.", account.getEmail()));
            e.printStackTrace();
            return false;
        }
        if (emailCheck != null) { //if an object with the same email exists
            log.debug(String.format(
                    "Email %s is in use.", account.getEmail()));
            throw new DuplicateEntityError(
                    String.format("Cannot create new account: Username %s is already in use.", emailCheck.getEmail()));
        } //email check done

        Account usernameCheck;
        try {
            usernameCheck = accountRepository.findAccountByUsername(account.getUsername());
        } catch (Exception e) {
            log.debug(String.format(
                    "Database query exception: Account fetch with username: %s failed.", account.getUsername()));
            e.printStackTrace();
            return false;
        }
        if (usernameCheck != null) { //if an object with the same email exits
            log.debug(String.format(
                    "Username %s is in use.", account.getUsername()));
            throw new DuplicateEntityError(
                    String.format("Cannot create new account: Email %s is already in use.", account.getUsername()));
        } //username check done
        return true;
    }
}
