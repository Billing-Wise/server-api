package site.billingwise.api.serverapi.global.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import site.billingwise.api.serverapi.domain.auth.CustomUserDetails;
import site.billingwise.api.serverapi.domain.user.User;

import java.util.Optional;

public class SecurityUtil {

    public static Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getUser());
        }

        return Optional.empty();
    }
}
