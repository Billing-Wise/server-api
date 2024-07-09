package site.billingwise.api.serverapi.global.response.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailureInfo {

    // common
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    INVALID_INPUT(400, "입력값이 유효하지 않습니다."),

    // auth
    UNAUTHORIZED_AUTH_CODE(401, "유효하지 않은 인증 코드입니다."),
    ALREADY_EXIST_USER(409, "이미 존재하는 사용자입니다."),
    WRONG_LOGIN_INFO(401, "잘못된 이메일 혹은 비밀번호입니다."),
    NOT_EXIST_USER(404, "존재하지 않는 사용자입니다."),

    // image
    INVALID_IMAGE(400, "사진 파일이 유효하지 않습니다.");

    private final Integer code;
    private final String message;

}
