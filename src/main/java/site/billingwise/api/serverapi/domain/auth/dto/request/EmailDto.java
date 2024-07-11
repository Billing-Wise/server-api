package site.billingwise.api.serverapi.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import site.billingwise.api.serverapi.domain.user.User;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

}
