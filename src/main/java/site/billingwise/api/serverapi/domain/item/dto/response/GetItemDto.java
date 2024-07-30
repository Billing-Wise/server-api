package site.billingwise.api.serverapi.domain.item.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.item.Item;

@Builder
@Getter
public class GetItemDto {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long contractCount;
    private Boolean isBasic;

    public static GetItemDto toDto(Item item) {
        GetItemDto getItemDto = GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .contractCount(item.getContractCount())
                .isBasic(item.getIsBasic())
                .build();

        return getItemDto;
    }
}
