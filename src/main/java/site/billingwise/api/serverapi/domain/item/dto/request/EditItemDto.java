package site.billingwise.api.serverapi.domain.item.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

	@NotBlank(message = "가격은 필수 입력값입니다.")
	@Pattern(regexp = "\\d+", message = "가격은 숫자만 허용됩니다.")
	@Min(value = 1, message = "가격은 1원 이상이어야합니다.")
	private String price;

	private String description;
}
