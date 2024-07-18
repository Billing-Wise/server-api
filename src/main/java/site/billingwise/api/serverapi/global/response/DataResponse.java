package site.billingwise.api.serverapi.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@Getter
public class DataResponse<T> extends BaseResponse {

    private final T data;

    public DataResponse(SuccessInfo successInfo, T data) {
        super(successInfo);
        this.data = data;
    }

    public DataResponse(FailureInfo failureInfo, T data) {
        super(failureInfo);
        this.data = data;
    }
}
