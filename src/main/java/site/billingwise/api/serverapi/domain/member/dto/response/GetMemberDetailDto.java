package site.billingwise.api.serverapi.domain.member.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMemberDetailDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String description;
    private Long contractCount;
    private Long unPaidCount;
    private Long totalInvoiceAmount;
    private Long totalUnpaidAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GetContractDetailDto> contractList;
}
