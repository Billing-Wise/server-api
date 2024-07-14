package site.billingwise.api.serverapi.domain.consent.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.member.Member;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterConsentDto {

    @NotBlank(message = "계좌주를 입력하세요.")
    private String owner;

    @NotBlank(message = "은행명을 입력하세요.")
    private String bank;

    @NotBlank(message = "계좌번호를 입력하세요.")
    @Pattern(regexp = "^\\d{10,}$", message = "유효한 계좌번호 형식이 아닙니다.")
    private String number;

    public ConsentAccount toEntity(Member member, String signUrl) {
        return ConsentAccount.builder()
                .id(member.getId())
                .member(member)
                .owner(this.owner)
                .bank(this.bank)
                .number(this.number)
                .signUrl(signUrl)
                .build();
    }
}
