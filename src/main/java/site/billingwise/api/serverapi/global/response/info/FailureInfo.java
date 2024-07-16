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
    NEW_PASSWORD_MISMATCH(400, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

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
    INVALID_IMAGE(419, "사진 파일이 유효하지 않습니다."),

    // file
    INVALID_FILE(419, "유효한 파일이 아닙니다"),
    INVALID_CELL_INPUT(400, "유효하지 않은 셀 형식입니다."),
    
    // item
    ITEM_NOT_FOUND(400, "해당 상품은 존재하지 않습니다."),

    // member
    NOT_EXIST_MEMBER(404, "존재하지 않는 회원입니다."),
    MEMBER_ACCESS_DENIED(403, "접근 권한이 없는 회원입니다."),

    // consent
    ALREADY_EXIST_CONSENT(409, "이미 동의서가 존재합니다."),
    NOT_EXIST_CONSENT(404, "동의서가 존재하지 않습니다."),

    // easy consent
    NOT_CMS(400, "실시간 CMS 계약이 아닙니다."),
    NOT_EASY_CONSENT_CONTRACT(400, "간편동의 계약이 아닙니다."),
    NOT_PENDING_CONTRACT(400, "간편동의 대기 중인 계약이 아닙니다."),

    // contract
    NOT_PROGRESS_CONTRACT(400, "진행 중인 계약이 아닙니다."),
    INVALID_DUE_CYCLE(400, "납부 기한은 약정일 이후여야합니다."),
    NOT_EXIST_CONTRACT(404, "존재하지 않는 계약입니다."),

    // invoice
    INVALID_CONTRACT_DATE(400, "약정일은 현재로부터 다음 날 이후여야합니다."),
    INVALID_DUE_DATE(400, "납부 기한은 약정일과 같거나 이후여야합니다."),
    INVALID_INVOICE_PAYMENTTYPE(400, "납부자 결제인 계약은 실시간 CMS를 결제 수단으로 선택할 수 없습니다."),
    DUPLICATE_INVOICE(400, "해당 월에 이미 청구 건이 존재합니다."),
    NOT_EXIST_INVOICE(400, "존재하지 않는 청구 정보입니다."),
    PAID_INVOICE(400, "이미 결제된 청구입니다."),
    DIFFERENT_MONTH(400, "약정일 수정 범위는 해당 연월입니다."),

    // payment type
    INVALID_PAYMENTTYPE(400, "유효하지 않은 결제 수단입니다."),

    // invoice type
    INVALID_INVOICETYPE(400, "유효하지 않은 청구 수단입니다.");

    private final Integer code;
    private final String message;

    public String getMessage(Object... args) {
        return String.format(this.message, args);
    }

}
