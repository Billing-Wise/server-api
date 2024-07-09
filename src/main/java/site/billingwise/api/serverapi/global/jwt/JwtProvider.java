package site.billingwise.api.serverapi.global.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import site.billingwise.api.serverapi.domain.auth.CustomUserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private static final Long ACCESS_TOKEN_EXPIRE_LENGTH = 1000L;    // 1h
    private static final Long REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24;  // 1d
    private final SecretKey secretKey;
    private static final String AUTHORITIES_KEY = "role";
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final UserDetailsService userDetailsService;

    public JwtProvider(@Value("${app.auth.token.secret-key}") String secret,
                       UserDetailsService userDetailsService,
                       RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.userDetailsService = userDetailsService;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_LENGTH);

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        String email = user.getUsername();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .signWith(secretKey)
                .setSubject(String.valueOf(email))
                .claim(AUTHORITIES_KEY, role)
                .setIssuer("billingwise")
                .setIssuedAt(now)
                .setExpiration(validity)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH);

        return Jwts.builder()
                .signWith(secretKey)
                .setIssuer("billingwise")
                .setIssuedAt(now)
                .setExpiration(validity)
                .compact();
    }

    public void addAccessToken(Authentication authentication, HttpServletResponse response) {
        String accessToken = createAccessToken(authentication);

        ResponseCookie cookie = ResponseCookie.from("access", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH / 1000)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void addRefreshToken(Authentication authentication, HttpServletResponse response) {
        String refreshToken = createRefreshToken();

        saveRefreshToken(authentication, refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH / 1000)
                .path("/")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void saveRefreshToken(Authentication authentication, String refreshToken) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(user.getId())
                .token(refreshToken)
                .expiredTime(REFRESH_TOKEN_EXPIRE_LENGTH)
                .build());
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }

    public boolean isExpiredToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
