package com.sikayetvar.convention.service;

import com.sikayetvar.convention.dto.AccountDTO;
import com.sikayetvar.convention.dto.CommentDTO;
import com.sikayetvar.convention.entity.Account;
import com.sikayetvar.convention.entity.Comment;
import com.sikayetvar.convention.exceptions.DuplicateEntityError;
import com.sikayetvar.convention.exceptions.MsDBOperationException;
import com.sikayetvar.convention.exceptions.ResourceNotFoundException;
import com.sikayetvar.convention.repository.AccountRepository;
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

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountService {

    final AccountRepository accountRepository;

    /**
     * Verilen id değerine sahip hesabı doner.
     *
     * @param id            Hesabın id değeri
     * @param commentLimit  Hesaba ait gösterilecek yorum sayısı limiti
     * @return              Hesap bilgileri
     */
    public AccountDTO getAccountById(Long id, Integer commentLimit) {
        Account account;

        try {
            account = accountRepository.findAccountByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get account");
        }

        if (account == null) {
            throw new ResourceNotFoundException(
                    "Account with id = " + id + " does not exist");
        }

        return new AccountDTO(
                account.getId(),
                account.getEmail(),
                account.getUsername(),
                account.getComments()
                        .stream().limit(commentLimit)
                        .map(comment -> new AccountDTO.Comment(
                                comment.getId(),
                                comment.getContent(),
                                comment.getOwner().getId(),
                                comment.getPrevious().getId(),
                                comment.getNext().getId()
                        ))
                        .collect(Collectors.toSet()),
                account.getCommentCount());
    }

    /**
     * Verilen AccountDTO hesap bilgilerini kayit eder.
     *
     * @param accountDTO    Olusturulmak istenen Account
     * @return              Kaydedilen Account'un id degeri
     */
    public Long createAccount(@NotNull AccountDTO accountDTO) {
        Account account =
                new Account(
                        accountDTO.getId(),
                        accountDTO.getEmail(),
                        accountDTO.getUsername(),
                        accountDTO.getComments()
                                .stream()
                                .map(commentDTO -> new Comment(
                                        commentDTO.getId(),
                                        commentDTO.getContent(),
                                        commentDTO.getOwnerId(),
                                        commentDTO.getPreviousId(),
                                        commentDTO.getNextId(),
                                        false
                                ))
                                .collect(Collectors.toSet()),
                        accountDTO.getCommentCount(),
                        false);

        if (accountRepository.existsByEmail(account.getEmail()))
            throw new DuplicateEntityError(
                    String.format("Cannot do operation: Email %s is already in use.", account.getEmail()));

        if (accountRepository.existsByUsername(account.getUsername()))
            throw new DuplicateEntityError(
                    String.format("Cannot do operation: Username %s is already in use.", account.getUsername()));

        Account savedAccount;
        try {
            savedAccount = accountRepository.save(account);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save account");
        }

        return savedAccount.getId();
    }

    /**
     * Account username'ini ve email'ini gunceller.
     *
     * @param id        Guncellenen Account'un id degeri
     * @param username  Yeni username
     * @param email     Yeni email
     * @return          Guncellenen Account'un id degeri
     */
    public Long updateUsernameEmail(Long id, String username, String email) {
        Account account;

        try {
            account = accountRepository.findAccountByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get account");
        }
        if (account == null) {
            throw new ResourceNotFoundException(
                    "Account with id = " + id + " does not exist");
        }

        if (email != null)
            account.setEmail(email);
        if (accountRepository.existsByEmail(account.getEmail()))
            throw new DuplicateEntityError(
                    String.format("Cannot do operation: Email %s is already in use.", account.getEmail()));

        if (username != null)
            account.setUsername(username);
        if (accountRepository.existsByUsername(account.getUsername()))
            throw new DuplicateEntityError(
                    String.format("Cannot do operation: Username %s is already in use.", account.getUsername()));

        Account savedAccount;
        try {
            savedAccount = accountRepository.save(account);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save account");
        }

        return savedAccount.getId();
    }

    /**
     * Hesabi siler. (soft-delete)
     *
     * @param id    Silinecek Account'un id degeri
     * @return
     */
    public Boolean deleteAccount(Long id) {
        Account account;

        try {
            account = accountRepository.findAccountByIdAndDeletedFalse(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get account");
        }
        if (account == null) {
            throw new ResourceNotFoundException(
                    "Account with id = " + id + " does not exist");
        }

        account.setDeleted(true);

        Account savedAccount;
        try {
            savedAccount = accountRepository.save(account);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to save account");
        }

        return true;
    }

    /**
     * Hesaplari sayfalandirilmis sekilde doner.
     * Default olarak id ile ascending siralar.
     *
     * @param page          Sayfa numarasi
     * @param size          Sayfadaki hesap sayisi
     * @param sortBy        Siralama icin field bilgisi
     * @param descending    Azalan siralama ayari
     * @param commentLimit  Hesabin gösterilecek yorum sayısına limit
     * @return              Istenen sayfadaki Account'larin listesi
     */
    public List<AccountDTO> getAccountsPaginated(Integer page,
                                                 Integer size,
                                                 String sortBy,
                                                 Boolean descending,
                                                 Integer commentLimit) {
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
        Page<Account> pagedResult;
        try {
            pagedResult =  accountRepository.findAll(pageable);
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
                    .map(account ->new AccountDTO(
                            account.getId(),
                            account.getEmail(),
                            account.getUsername(),
                            account.getComments()
                                    .stream().limit(commentLimit)
                                    .map(comment -> new AccountDTO.Comment(
                                            comment.getId(),
                                            comment.getContent(),
                                            comment.getOwner().getId(),
                                            comment.getPrevious().getId(),
                                            comment.getNext().getId()
                                    ))
                                    .collect(Collectors.toSet()),
                            account.getCommentCount())
                    ).collect(Collectors.toList());
        }
        return new ArrayList<AccountDTO>();
    }

    /**
     * Silinmis hesabi hesabi geri getirir.
     *
     * @param id    Geri getirilecek hesbain id degeri
     * @return      Geri getirilen hesabin id degeri
     */
    @Transactional
    public Long retrieveAccount(Long id) {
        Account account;

        try {
            accountRepository.retrieveDeletedAccount(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get account");
        }

        return id;
    }

    /**
     * Butun hesaplari geri getirir.
     *
     * @return
     */
    @Transactional
    public void retrieveAccounts() {

        try {
            accountRepository.retrieveDeletedAccounts();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MsDBOperationException("Unable to get account");
        }
    }

}
