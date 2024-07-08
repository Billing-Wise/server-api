package site.billingwise.api.serverapi.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterDto registerDto) {
        Client client = clientRepository.findByAuthCode(registerDto.getAuthCode())
                .orElseThrow(() -> new GlobalException(FailureInfo.UNAUTHORIZED_AUTH_CODE));

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_USER);
        }
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(registerDto.toEntity(client));
    }
}
