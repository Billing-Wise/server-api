package site.billingwise.api.serverapi.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.user.Client;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberDto {
    @NotBlank(message = "회원명은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^010\\d{4}\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;

    private String description;

    public Member toEntity(Client client) {
        Member member = Member.builder()
                .client(client)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .description(this.description)
                .build();

        return member;
    }
}
