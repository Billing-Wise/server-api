package site.billingwise.api.serverapi.domain.setting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetBasicPaymentsDto {
    private List<Long> itemIdList = new ArrayList<>();
}
