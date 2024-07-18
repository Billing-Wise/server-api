package site.billingwise.api.serverapi.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.user.User;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCurrentUserDto {

    private Long clientId;
    private String clientName;
    private String clientPhone;
    private Long userId;
    private String userEmail;
    private String userName;
    private String userPhone;

    public static GetCurrentUserDto toDto(User user) {
        return GetCurrentUserDto.builder()
                .clientId(user.getClient().getId())
                .clientName(user.getClient().getName())
                .clientPhone(user.getClient().getPhone())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .userPhone(user.getPhone())
                .build();
    }
}
