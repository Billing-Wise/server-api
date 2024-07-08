package site.billingwise.api.serverapi.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;

@Getter
@Builder
public class RegisterDto {

    @NotBlank(message = "고객 코드를 입력해주세요.")
    private String authCode;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 알파벳, 숫자와 특수문자를 모두 포함해야하며 8자리 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
            message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;

    public void setPassword(String password) {
        this.password = password;
    }

    public User toEntity(Client client) {
        return User.builder()
                .client(client)
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .phone(this.phone)
                .build();
    }

}
