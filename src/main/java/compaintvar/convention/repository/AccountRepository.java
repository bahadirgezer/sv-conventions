package compaintvar.convention.repository;

import compaintvar.convention.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountByIdAndDeletedFalse(Long id);

    Account findAccountByUsernameAndDeletedFalse(String username);

    Account findAccountByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Modifying
    @Query(value = "UPDATE account SET deleted = 0", nativeQuery = true)
    void retrieveDeletedAccounts();

    @Modifying
    @Query(value = "UPDATE account SET deleted = 0 WHERE id =?1", nativeQuery = true)
    void retrieveDeletedAccount(Long id);
}
