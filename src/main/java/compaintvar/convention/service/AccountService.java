package compaintvar.convention.service;

import compaintvar.convention.dto.AccountDTO;
import compaintvar.convention.entity.Account;
import compaintvar.convention.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;

    public AccountDTO getAccountById(Long id) {
        log.debug(String.format("Getting account %d.", id));
        Account account;

        return new AccountDTO();
    }
}
