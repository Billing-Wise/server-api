package site.billingwise.api.serverapi.global.response.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessInfo {

    //Auth
    REGISTER("사용자 등록이 성공하였습니다."),
    LOGIN("로그인이 성공하였습니다."),

    // Item
    ITEM_CREATED("상품 생성이 성공하였습니다."),
    ITEM_EDITED("상품 정보 수정이 성공하였습니다"),
    ITEM_IMAGE_EDITED("상품 이미지 수정이 성공하였습니다.");

    private final Integer code = 200;
    private final String message;
}
