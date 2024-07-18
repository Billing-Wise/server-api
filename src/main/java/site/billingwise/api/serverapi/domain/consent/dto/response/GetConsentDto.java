package site.billingwise.api.serverapi.domain.consent.dto.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.Get;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;

import java.time.LocalDateTime;

@Builder
@Getter
public class GetConsentDto {

    private Long memberId;

    private String owner;

    private String bank;

    private String number;

    private String signUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetConsentDto toDto(ConsentAccount consentAccount) {
        return GetConsentDto.builder()
                .memberId(consentAccount.getId())
                .owner(consentAccount.getOwner())
                .bank(consentAccount.getBank())
                .number(consentAccount.getNumber())
                .signUrl(consentAccount.getSignUrl())
                .createdAt(consentAccount.getCreatedAt())
                .updatedAt(consentAccount.getUpdatedAt())
                .build();
    }
}
