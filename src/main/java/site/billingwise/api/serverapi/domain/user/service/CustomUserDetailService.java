package site.billingwise.api.serverapi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.billingwise.api.serverapi.domain.user.CustomUserDetails;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(FailureInfo.NOT_EXIST_USER.getMessage()));
        return new CustomUserDetails(user);
    }


}
