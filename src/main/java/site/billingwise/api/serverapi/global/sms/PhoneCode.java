package site.billingwise.api.serverapi.global.sms;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "phone_code")
public class PhoneCode {

    @Id
    private String phone;
    private Integer code;

    @TimeToLive
    private long expiredTime;
}