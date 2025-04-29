package dev.hamze.nanoexchange.common.core.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum PersonalityTypeEnum {

    LOCAL_REAL(0),
    LOCAL_LEGAL(1),
    FOREIGN_REAL(2);

    private static final Map<Integer, PersonalityTypeEnum> VALUE_MAP = new HashMap<>();

    static {
        for (PersonalityTypeEnum value : PersonalityTypeEnum.values()) {
            VALUE_MAP.put(value.getTypeCode(), value);
        }
    }

    private final int typeCode;

    PersonalityTypeEnum(int typeCode) {
        this.typeCode = typeCode;
    }

    @JsonCreator
    public static PersonalityTypeEnum getByValue(Integer typeCode) {
        if (typeCode == null) {
            return null;
        }

        PersonalityTypeEnum value = VALUE_MAP.get(typeCode);
        if (value == null) {
            throw new IllegalArgumentException("Bad type code [" + typeCode + "] is provided.");
        }

        return value;
    }

    @JsonValue
    public int getTypeCode() {
        return typeCode;
    }
}
