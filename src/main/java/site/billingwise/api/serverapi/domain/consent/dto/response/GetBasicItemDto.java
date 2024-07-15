package site.billingwise.api.serverapi.domain.consent.dto.response;

import lombok.Builder;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.item.Item;

@Builder
@Getter
public class GetBasicItemDto {
    private Long id;
    private String name;
    private Long price;
    private String imageUrl;

    public static GetBasicItemDto toDto(Item item) {
        return GetBasicItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
