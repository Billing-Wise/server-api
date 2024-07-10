package site.billingwise.api.serverapi.global.mail;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import site.billingwise.api.serverapi.global.jwt.RefreshToken;
@Repository

public interface MailCodeRedisRepository extends CrudRepository<MailCode, String> {
}
