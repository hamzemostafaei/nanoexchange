package dev.hamze.nanoexchange.presentation.handler;

import dev.hamze.nanoexchange.common.core.data.ErrorCodeEnum;
import dev.hamze.nanoexchange.common.core.data.ErrorDTO;
import dev.hamze.nanoexchange.presentation.api.data.BaseEdgeResponseDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseEdgeResponseDTO handleException(Exception exception) {
        if (log.isErrorEnabled()) {
            log.error(exception.getMessage(), exception);
        }

        BaseEdgeResponseDTO response = new BaseEdgeResponseDTO();
        response.addError(new ErrorDTO(ErrorCodeEnum.INTERNAL_SERVICE_ERROR, "InternalServerError"));

        return response;
    }

    @ResponseBody
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseEdgeResponseDTO handleException(ValidationException validationException) {
        BaseEdgeResponseDTO response = new BaseEdgeResponseDTO();
        if (validationException instanceof ConstraintViolationException constraintViolationException) {

            List<ErrorDTO> errors = constraintViolationException.getConstraintViolations().stream()
                    .map(violation -> {
                        String fieldName = getFinalFieldName(violation.getPropertyPath());
                        String errorMessage = violation.getMessage();
                        return new ErrorDTO(ErrorCodeEnum.DATA_FORMAT_MISMATCH, String.format("%s %s", fieldName, errorMessage), fieldName);
                    })
                    .toList();

            if (log.isErrorEnabled()) {
                log.error("Violated fields: {}", errors);
            }
            response.setErrors(errors);
        } else {
            String exceptionMessage = validationException.getMessage();
            if (log.isErrorEnabled()) {
                log.error(exceptionMessage, validationException);
            }
            response.addError(new ErrorDTO(ErrorCodeEnum.INCONSISTENT_DATA, exceptionMessage, "ConstraintViolationException"));
        }

        return response;
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseEdgeResponseDTO handleException(MethodArgumentNotValidException exception) {

        List<ErrorDTO> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorDTO(ErrorCodeEnum.DATA_FORMAT_MISMATCH, fieldError.getDefaultMessage(), fieldError.getField()))
                .toList();

        if (log.isErrorEnabled()) {
            log.error("Validation failed: {}", errors);
        }

        BaseEdgeResponseDTO response = new BaseEdgeResponseDTO();
        response.setErrors(errors);

        return response;
    }

    private String getFinalFieldName(Path propertyPath) {
        String fieldName = null;
        for (Path.Node node : propertyPath) {
            fieldName = node.getName();
        }
        return fieldName;
    }

}