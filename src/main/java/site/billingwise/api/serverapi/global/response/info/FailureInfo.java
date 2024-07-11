package site.billingwise.api.serverapi.global.response.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailureInfo {

    // common
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    INVALID_INPUT(400, "입력값이 유효하지 않습니다."),
    ACCESS_DENIED(403, "권한이 없습니다."),

    // jwt
    ACCESS_TOKEN_NULL(401, "액세스 토큰이 누락되었습니다."),
    INVALID_ACCESS_TOKEN(401, "유효하지 않은 액세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(419, "만료된 액세스 토큰입니다."),
    REFRESH_TOKEN_NULL(401, "리프레시 토큰이 누락되었습니다."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),

    // auth
    UNAUTHORIZED_AUTH_CODE(401, "유효하지 않은 인증 코드입니다."),
    ALREADY_EXIST_USER(409, "이미 존재하는 사용자입니다."),
    CLIENT_NOT_FOUND(401, "존재하지 않는 고객입니다."),
    WRONG_LOGIN_INFO(401, "잘못된 이메일 혹은 비밀번호입니다."),
    NOT_EXIST_USER(404, "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_EMAIL(409, "이미 사용 중인 이메일입니다."),

    // mail
    SEND_MAIL_CODE_FAIL(554, "메일 인증코드 전송을 실패했습니다"),
    INVALID_MAIL_CODE(401, "유효하지 않은 메일 인증 코드입니다."),

    // phone
    INVALID_PHONE_CODE(401, "유효하지 않은 전화번호 인증 코드입니다."),

    // image
    INVALID_IMAGE(400, "사진 파일이 유효하지 않습니다."),
    
    // item
    ITEM_NOT_FOUND(400, "해당 상품은 존재하지 않습니다."),
    ITEM_ACCESS_DENIED(403, "접근 권한이 없는 상품입니다."),

    // member
    NOT_EXIST_MEMBER(400, "존재하지 않는 회원입니다."),
    MEMBER_ACCESS_DENIED(403, "접근 권한이 없는 회원입니다.");

    private final Integer code;
    private final String message;

}
