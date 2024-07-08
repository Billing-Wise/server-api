package site.billingwise.api.serverapi.global.exception;

import lombok.Getter;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Getter
public class GlobalException extends RuntimeException {
    private final FailureInfo failureInfo;
    public GlobalException(FailureInfo failureInfo) {
        super(failureInfo.getMessage());
        this.failureInfo = failureInfo;
    }
}
