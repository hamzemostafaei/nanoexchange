package dev.hamze.nanoexchange.common.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCodeEnum {

    GENERAL_ERROR(0),
    MANDATORY_FIELD(1),
    DATA_FORMAT_MISMATCH(2),
    DUPLICATE_DATA(3),
    INCONSISTENT_DATA(4),
    SERVICE_CONTRACTS_VIOLATION(5),
    DATA_NOT_FOUND(6),
    NOT_ENOUGH_RESOURCES(7),
    not_authorized_for_data(8),
    WITHDRAWAL_OR_SETTLEMENT_ERROR(9),
    SERVICE_TEMPORARY_DOWN(10),
    INTERNAL_SERVICE_ERROR(11),
    EXTERNAL_SERVICE_TEMPORARY_DOWN(12),
    NO_RESPONSE_FROM_EXTERNAL_SERVICE(13),
    PLEASE_REPEAT_THE_REQUEST(14),
    UNABLE_TO_PROVIDE_THE_REQUESTED_SERVICE(15),
    SECURITY_ERROR(16),
    OUT_OF_BOUNDS_DATA(17),
    INACTIVE_REFERENCE(18),
    EXPIRED_REFERENCE(19),
    MANUAL_REJECTION(20);

    private static final Map<Integer, ErrorCodeEnum> VALUE_MAP = new HashMap<>();

    static {
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            VALUE_MAP.put(value.getErrorCode(), value);
        }
    }

    private final int errorCode;

    ErrorCodeEnum(int errorCode) {
        this.errorCode = errorCode;
    }

    @JsonCreator
    public static ErrorCodeEnum getByValue(Integer errorCode) {
        ErrorCodeEnum value = VALUE_MAP.get(errorCode);
        if (value == null) {
            throw new IllegalArgumentException("Bad error code [" + errorCode + "] is provided.");
        }

        return value;
    }

    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
