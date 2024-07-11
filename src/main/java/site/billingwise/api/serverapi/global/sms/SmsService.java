package site.billingwise.api.serverapi.global.sms;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.global.mail.EmailCode;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    private static final Long PHONE_CODE_EXPIRE_LENGTH = 1L * 60 * 3;    // 3m

    private final PhoneCodeRedisRepository phoneCodeRedisRepository;

    private DefaultMessageService messageService;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${coolsms.api.sender}")
    private String sender;

    @Value("${coolsms.api.url}")
    private String url;


    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, url);
    }

    public void sendPhoneCode(String to) {

        Integer code = new Random().nextInt(900000) + 100000;

        phoneCodeRedisRepository.save(
                PhoneCode.builder()
                        .phone(to)
                        .code(code)
                        .expiredTime(PHONE_CODE_EXPIRE_LENGTH)
                        .build()
        );

        sendOne(to, code);
    }

    public SingleMessageSentResponse sendOne(String to, Integer verificationCode) {
        Message message = new Message();

        message.setFrom(sender);
        message.setTo(to);
        message.setText("[빌링와이즈] 아래의 인증번호를 입력해주세요\n" + verificationCode);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }
}
