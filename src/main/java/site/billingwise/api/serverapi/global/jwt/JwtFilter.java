package site.billingwise.api.serverapi.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.CookieUtil;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Cookie accessCookie = CookieUtil.getCookie(request, "access");

        if (accessCookie == null || !StringUtils.hasText(accessCookie.getValue())) {
            request.setAttribute("exception", FailureInfo.ACCESS_TOKEN_NULL);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessCookie.getValue();

        if (!jwtProvider.validateToken(accessToken)) {
            if (jwtProvider.isExpiredToken(accessToken)) {
                request.setAttribute("exception", FailureInfo.EXPIRED_ACCESS_TOKEN);
            } else {
                request.setAttribute("exception", FailureInfo.NOT_VALID_ACCESS_TOKEN);
            }
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
