package site.billingwise.api.serverapi.domain.consent.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentWithNonMemberDto {

    @NotBlank(message = "이름을 입력해주세요.")
    private String memberName;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String memberEmail;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{4}\\d{4}$",
            message = "유효한 전화번호 형식이 아닙니다.")
    private String memberPhone;

    @NotNull(message = "상품 아이디를 입력해주세요.")
    private Long itemId;

    @NotNull(message = "상품 개수를 입력해주세요.")
    @Min(value = 1, message = "상품 수량은 1 이상이어야 합니다.")
    private Integer itemAmount;

    @NotNull(message = "정기 여부를 입력해주세요.")
    private Boolean isSubscription;

    @NotNull(message = "약정일은 필수 입력값입니다.")
    @Min(value = 1, message = "약정일은 1일~30일 사이로 지정하여야합니다.")
    @Max(value = 30, message = "약정일은 1일~30일 사이로 지정하여야합니다.")
    private Integer contractCycle;

    @NotBlank(message = "은행명을 입력하세요.")
    private String accountBank;

    @NotBlank(message = "계좌주를 입력하세요.")
    private String accountOwner;

    @NotBlank(message = "계좌번호를 입력하세요.")
    @Pattern(regexp = "^\\d{10,}$", message = "유효한 계좌번호 형식이 아닙니다.")
    private String accountNumber;



}
