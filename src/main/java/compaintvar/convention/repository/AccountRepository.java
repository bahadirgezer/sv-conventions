package compaintvar.convention.repository;

import compaintvar.convention.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountByIdAndDeletedFalse(Long id);

    Account findAccountByUsernameAndDeletedFalse(String username);

    Account findAccountByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query(value = "UPDATE comment SET deleted = false", nativeQuery = true)
    void retrieveDeletedAccounts();
}
