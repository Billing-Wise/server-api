package site.billingwise.api.serverapi.domain.item.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

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

}
