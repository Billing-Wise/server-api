package site.billingwise.api.serverapi.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditItemDto {

	@NotBlank(message = "상품명은 필수 입력값입니다.")
	private String name;

	@NotNull(message = "가격은 필수 입력값입니다.")
	private Long price;

	private String description;
}
