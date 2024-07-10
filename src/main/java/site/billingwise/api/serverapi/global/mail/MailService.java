package site.billingwise.api.serverapi.global.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private static final Long MAIL_CODE_EXPIRE_LENGTH = 1000L * 60 * 3;    // 3m

    private final JavaMailSender mailSender;

    private final MailCodeRedisRepository mailCodeRedisRepository;

    @Value("${spring.mail.username}")
    private String fromMail;

    private int code;

    public void sendMailCode(String email) {
        try {
            mailCodeRedisRepository.save(
                    MailCode.builder()
                            .email(email)
                            .code(code)
                            .expiredTime(MAIL_CODE_EXPIRE_LENGTH)
                            .build());

            MimeMessage message = createMailCode(email);
            mailSender.send(message);
        }catch(Exception e) {
            log.error("exception", e);
            throw new GlobalException(FailureInfo.SEND_MAIL_CODE_FAIL);
        }
    }

    public MimeMessage createMailCode(String email){
        code = new Random().nextInt(900000) + 100000;
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setFrom(fromMail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[빌링와이즈] 이메일 인증 코드");
            String body = "";
            body += "<h1>" + "안녕하세요." + "</h1>";
            body += "<h1>" + "빌링와이즈 입니다." + "</h1>";
            body += "<h3>" + "요청하신 이메일 인증 코드입니다." + "</h3><br>";

            body += "<div align='center' style='border:1px solid black; font-family:verdana;'>";
            body += "<h2>" + "이메일 인증 코드" + "</h2>";
            body += "<h1 style='color:blue'>" + code + "</h1>";
            body += "</div><br>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");

        } catch (Exception e) {
            log.error("exception", e);
            throw new GlobalException(FailureInfo.SEND_MAIL_CODE_FAIL);
        }

        return message;
    }
}
