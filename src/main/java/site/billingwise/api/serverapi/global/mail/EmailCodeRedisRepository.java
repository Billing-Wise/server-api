package site.billingwise.api.serverapi.global.mail;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface EmailCodeRedisRepository extends CrudRepository<EmailCode, String> {
}
