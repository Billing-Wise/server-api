package site.billingwise.api.serverapi.global.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import site.billingwise.api.serverapi.domain.user.CustomUserDetails;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.Optional;

@UtilityClass
public class SecurityUtil {

    public static Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getUser());
        }

        return Optional.empty();
    }

    public static Client getCurrentClient() {
        return getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER))
                .getClient();
    }


}
