package site.billingwise.api.serverapi.global.sms;

import org.springframework.data.repository.CrudRepository;
import site.billingwise.api.serverapi.global.mail.EmailCode;

public interface PhoneCodeRedisRepository extends CrudRepository<PhoneCode, String> {
}
