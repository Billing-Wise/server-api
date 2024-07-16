package site.billingwise.api.serverapi.global.response.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessInfo {

    //Auth
    REGISTER("사용자 등록이 성공하였습니다."),
    LOGIN("로그인이 성공하였습니다."),
    LOGOUT("로그아웃이 성공하였습니다."),
    AVAILABLE_EMAIL("사용 가능한 이메일입니다."),
    SEND_MAIL_CODE("이메일 인증코드가 전송되었습다"),
    AUTHENTICATE_EMAIL("이메일 인증이 성공하였습니다."),
    SEND_PHONE_CODE("전화번호 인증코드가 전송되었습니다."),
    AUTHENTICATE_PHONE("전화번호 인증이 성공하였습니다."),
    FIND_EMAIL("이메일 찾기가 성공하였습니다."),
    FIND_PASSWORD("비밀번호 찾기가 성공하였습니다."),

    // Jwt
    REISSUE("토큰 재발급이 성공하였습니다."),
    
    // setting
    SET_ITEMS_TO_BASIC("간편 동의 상품 설정이 완료되었습니다."),

    // file
    FILE_UPLOADED("파일이 업로드되었습니다."),

    // Item
    ITEM_CREATED("상품이 성공적으로 생성되었습니다."),
    ITEM_EDITED("상품 정보가 성공적으로 수정되었습니다."),
    ITEM_IMAGE_EDITED("상품 이미지가 성공적으로 수정되었습니다."),
    ITEM_DELETED("상품이 성공적으로 삭제되었습니다."),
    ITEM_LOADED("상품 정보를 성공적으로 조회하였습니다."),

    // Member
    MEMBER_CREATED("회원이 성공적으로 등록되었습니다."),
    MEMBER_UPDATED("회원 정보가 성공적으로 수정되었습니다."),
    MEMBER_DELETED("회원 정보가 성공적으로 삭제되었습니다."),
    MEMBER_LOADED("회원 정보를 성공적으로 조회하였습니다."),

    // Contract
    CONTRACT_CREATED("계약 정보가 성공적으로 등록되었습니다."),
    CONTRACT_EDITED("계약 정보가 성공적으로 수정되었습니다."),
    CONTRACT_DELETED("계약 정보가 성공적으로 삭제되었습니다."),
    CONTRACT_LOADED("계약 정보를 성공적으로 조회하였습니다."),

    // Consent
    CONSENT_REGISTERED("동의정보 등록이 완료되었습니다."),
    GET_CONSENT("동의정보 조회가 성공하였습니다."),
    CONSENT_EDITED("동의정보 수정이 완료되었습니다."),
    CONSENT_SIGN_IMAGE_EDITED("동의 서명 수정이 완료되었습니다."),
    CONSENT_DELETED("동의정보 삭제가 완료되었습니다."),

    // Easy Consent
    GET_BASIC_ITEM_LIST("비회원 간편동의 상품목록조회가 성공하였습니다."),
    GET_EASY_CONSENT_CONTRACT_INFO("회원 간편동의 계약정보 조회가 성공하였습니다."),
    CONSENT_WITH_NON_MEMBER("비회원 간편동의가 성공하였습니다.");

    private final Integer code = 200;
    private final String message;
}
