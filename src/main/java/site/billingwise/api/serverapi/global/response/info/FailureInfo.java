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
    CLIENT_NOT_FOUND(401, "존재하지 않는 고객입니다."),

    // image
    INVALID_IMAGE(400, "사진 파일이 유효하지 않습니다."),
    
    // item
    ITEM_NOT_FOUND(400, "해당 상품은 존재하지 않습니다."),
    ITEM_ACCESS_DENIED(403, "접근 권한이 없는 상품입니다.");

    private final Integer code;
    private final String message;

}
