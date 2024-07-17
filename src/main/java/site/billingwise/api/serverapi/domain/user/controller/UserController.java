package site.billingwise.api.serverapi.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import site.billingwise.api.serverapi.domain.user.dto.response.GetCurrentUserDto;
import site.billingwise.api.serverapi.domain.user.service.UserService;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public DataResponse<GetCurrentUserDto> getCurrentUser() {
        return new DataResponse<>(SuccessInfo.GET_CURRENT_USER,
                userService.getCurrentUser());
    }
}
