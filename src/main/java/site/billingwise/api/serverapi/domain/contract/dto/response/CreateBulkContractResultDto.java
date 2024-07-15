package site.billingwise.api.serverapi.domain.contract.dto.response;

import java.util.List;

import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkContractResultDto {

    @Setter
    private boolean isSuccess;

    private List<CreateContractDto> contractList;
    
    private List<String> errorList;
}
