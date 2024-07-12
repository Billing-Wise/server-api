package site.billingwise.api.serverapi.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{4}\\d{4}$",
            message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;
}
