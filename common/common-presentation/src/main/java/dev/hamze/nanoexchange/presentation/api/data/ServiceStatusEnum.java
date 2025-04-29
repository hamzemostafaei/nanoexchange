package dev.hamze.nanoexchange.presentation.api.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ServiceStatusEnum {

    BEING_PROCESSED(0),
    SUCCESSFUL(1),
    UNSUCCESSFUL(2),
    RETRY_LATER(3),
    CONTACT_SYSTEM_ADMINISTRATOR(4);

    private static final Map<Integer, ServiceStatusEnum> VALUE_MAP = new HashMap<>();

    static {
        for (ServiceStatusEnum value : ServiceStatusEnum.values()) {
            VALUE_MAP.put(value.getStatusCode(), value);
        }
    }

    private final int statusCode;

    ServiceStatusEnum(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonCreator
    public static ServiceStatusEnum getByValue(Integer statusCode) {
        if (statusCode == null) {
            return null;
        }

        ServiceStatusEnum value = VALUE_MAP.get(statusCode);
        if (value == null) {
            throw new IllegalArgumentException("Bad status code [" + statusCode + "] is provided.");
        }

        return value;
    }

    public static ServiceStatusEnum getBySuccessFlag(Boolean successful) {
        if (successful == null) {
            return null;
        }

        if (successful) {
            return ServiceStatusEnum.SUCCESSFUL;
        } else {
            return ServiceStatusEnum.UNSUCCESSFUL;
        }
    }

    @JsonValue
    public int getStatusCode() {
        return statusCode;
    }
}
