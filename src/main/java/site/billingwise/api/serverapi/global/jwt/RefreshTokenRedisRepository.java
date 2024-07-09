package site.billingwise.api.serverapi.global.jwt;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {

}
