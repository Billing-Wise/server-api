package site.billingwise.api.serverapi.domain.contract.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractItemDto {
    private Long id;
    private String name;
    private Long price;
    private Integer amount;
}
