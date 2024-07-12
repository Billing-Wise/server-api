package site.billingwise.api.serverapi.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindEmailDto {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{4}\\d{4}$",
            message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;

    @NotNull(message = "전화번호 인증 코드를 입력해주세요.")
    private Integer code;
}
