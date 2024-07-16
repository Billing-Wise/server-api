package site.billingwise.api.serverapi.domain.invoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceMemberDto {
    private Long memberId;
    private String name;
    private String email;
    private String phone;
}
