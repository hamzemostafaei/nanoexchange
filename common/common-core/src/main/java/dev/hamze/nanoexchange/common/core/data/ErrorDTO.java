package dev.hamze.nanoexchange.common.core.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDTO implements Serializable {

    private static final String LINE_SEPARATOR;
    private static final ObjectMapper OBJECT_MAPPER;
    private static final ObjectWriter OBJECT_WRITER;

    static {
        LINE_SEPARATOR = System.lineSeparator();

        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        OBJECT_WRITER = OBJECT_MAPPER.writerFor(ErrorDTO.class);
    }

    @JsonProperty("errorCode")
    private ErrorCodeEnum errorCode;

    @JsonProperty("errorDescription")
    private String errorDescription;

    @JsonProperty("referenceName")
    private String referenceName;

    @JsonProperty("originalValue")
    private String originalValue;

    @JsonProperty("extraData")
    private String extraData;

    public ErrorDTO() {
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String referenceName) {
        this.errorCode = errorCode;
        this.referenceName = referenceName;
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String referenceName, Object fieldValue) {
        this.errorCode = errorCode;
        this.referenceName = referenceName;

        try {
            this.originalValue = OBJECT_MAPPER.writeValueAsString(fieldValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String errorDescription, String referenceName) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.referenceName = referenceName;
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String errorDescription, String referenceName, String originalValue) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.referenceName = referenceName;
        this.originalValue = originalValue;
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String errorDescription, String referenceName, Object fieldValue) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.referenceName = referenceName;

        try {
            this.originalValue = OBJECT_MAPPER.writeValueAsString(fieldValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String errorDescription, String referenceName, String originalValue, String extraData) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.referenceName = referenceName;
        this.originalValue = originalValue;
        this.extraData = extraData;
    }

    public ErrorDTO(ErrorCodeEnum errorCode, String errorDescription, String referenceName, Object fieldValue, String extraData) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.referenceName = referenceName;

        try {
            this.originalValue = OBJECT_MAPPER.writeValueAsString(fieldValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        this.extraData = extraData;
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String referenceName) {
        return new ErrorDTO(errorCode, referenceName);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String referenceName, Object fieldValue) {
        return new ErrorDTO(errorCode, referenceName,fieldValue);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName) {
        return new ErrorDTO(errorCode, errorDescription,referenceName);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName, String originalValue) {
        return new ErrorDTO(errorCode, errorDescription,referenceName,originalValue);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName, Object fieldValue) {
        return new ErrorDTO(errorCode, errorDescription,referenceName,fieldValue);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName, String originalValue, String extraData) {
        return new ErrorDTO(errorCode, errorDescription,referenceName,originalValue,extraData);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName, Object fieldValue, String extraData) {
        return new ErrorDTO(errorCode, errorDescription,referenceName,fieldValue,extraData);
    }

    public static ErrorDTO of(ErrorCodeEnum errorCode, String errorDescription, String referenceName, Object... args) {
        return new ErrorDTO(errorCode, errorDescription, referenceName, args);
    }

    public static String objectToString(Object object) {
        return LINE_SEPARATOR + objectToJSON(object);
    }

    private static String objectToJSON(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "JSON DUMP ERROR " + e.getMessage();
        }
    }

    @Override
    public String toString() {
        return objectToString(this);
    }
}