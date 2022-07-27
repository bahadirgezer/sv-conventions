package compaintvar.convention.repository;

import compaintvar.convention.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountById(Long id);
}
