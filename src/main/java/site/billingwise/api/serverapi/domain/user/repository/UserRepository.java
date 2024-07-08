package site.billingwise.api.serverapi.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long>  {
    boolean existsByEmail(String email);
}
