package site.billingwise.api.serverapi.global.mail;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "mail_code")
public class MailCode {

    @Id
    private String email;
    private int code;

    @TimeToLive
    private long expiredTime;
}
