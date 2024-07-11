package site.billingwise.api.serverapi.domain.member.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetContractDetailDto {
    private Long clientId;
    private List<GetInvoiceDetailDto> invoiceList;
}
