package site.billingwise.api.serverapi.domain.member.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBulkResultDto {
    private boolean isSuccess;
    private List<CreateMemberDto> memberList;
    private List<String> errorList;
}
