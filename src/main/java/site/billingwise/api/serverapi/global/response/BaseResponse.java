package site.billingwise.api.serverapi.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@Getter
@RequiredArgsConstructor
public class BaseResponse {

    private final Integer code;
    private final String message;

    public BaseResponse(SuccessInfo successInfo) {
        this.code = successInfo.getCode();
        this.message = successInfo.getMessage();
    }

    public BaseResponse(FailureInfo failureInfo) {
        this.code = failureInfo.getCode();
        this.message = failureInfo.getMessage();
    }
}
