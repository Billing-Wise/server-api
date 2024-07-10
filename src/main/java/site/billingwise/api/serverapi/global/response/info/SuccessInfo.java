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

    // Jwt
    REISSUE("토큰 재발급이 성공하였습니다."),

    // Item
    ITEM_CREATED("상품이 성공적으로 생성되었습니다."),
    ITEM_EDITED("상품 정보가 성공적으로 수정되었습니다."),
    ITEM_IMAGE_EDITED("상품 이미지가 성공적으로 수정되었습니다."),
    ITEM_DELETED("상품이 성공적으로 삭제되었습니다."),
    ITEM_LOADED("상품 정보를 성공적으로 불러왔습니다."),

    // Member
    MEMBER_CREATED("회원이 성공적으로 등록되었습니다."),
    MEMBER_UPDATED("회원 정보가 성공적으로 수정되었습니다."),
    MEMBER_DELETED("회원 정보가 성공적으로 삭제되었습니다.");

    private final Integer code = 200;
    private final String message;
}
