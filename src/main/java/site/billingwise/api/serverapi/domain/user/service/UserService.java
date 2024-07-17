package site.billingwise.api.serverapi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.dto.response.GetCurrentUserDto;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@RequiredArgsConstructor
@Service
public class UserService {
    private final ClientRepository clientRepository;
    public GetCurrentUserDto getCurrentUser() {
        User user = SecurityUtil.getCurrentUser()
                        .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Client client = clientRepository.findById(user.getClient().getId())
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CLIENT));

        user.setClient(client);

        return GetCurrentUserDto.toDto(user);
    }
}
