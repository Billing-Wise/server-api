package site.billingwise.api.serverapi.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.user.Client;

@Getter
@Builder
public class CreateItemDto {

	@NotBlank(message = "상품명은 필수 입력값입니다.")
	private String name;

	@NotNull(message = "가격은 필수 입력값입니다.")
	private Long price;

	private String description;

	public Item toEntity(Client client, String imageUrl) {
		Item item = Item.builder()
						.client(client)
						.name(name)
						.description(description)
						.price(price)
						.imageUrl(imageUrl)
						.isBasic(true)
						.build();
		return item;
	}
}
