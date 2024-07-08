package site.billingwise.api.serverapi.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import site.billingwise.api.serverapi.domain.user.Client;

public interface ClientRepository extends JpaRepository<Client, Long>{
    public Optional<Client> findById(Long id);
}
