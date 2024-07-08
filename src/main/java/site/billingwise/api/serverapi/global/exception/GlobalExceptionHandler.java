package site.billingwise.api.serverapi.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<BaseResponse> globalException(GlobalException ex) {
        log.error("globalException", ex);
        return ResponseEntity
                .status(ex.getFailureInfo().getCode())
                .body(new BaseResponse(ex.getFailureInfo()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected BaseResponse internalSeverException(Exception ex) {
        log.error("internalSeverException", ex);
        return new BaseResponse(FailureInfo.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected BaseResponse invalidInputException(MethodArgumentNotValidException ex) {
        log.error("invalidInputException" + ex);
        return new BaseResponse(
                FailureInfo.INVALID_INPUT.getCode(),
                ex.getFieldError().getDefaultMessage());

    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected BaseResponse invalidInputException(ConstraintViolationException ex) {
        log.error("invalidInputException" + ex);
        return new BaseResponse(
                FailureInfo.INVALID_INPUT.getCode(),
                ex.getConstraintViolations().stream().toList().get(0).getMessage()
        );
    }
}
