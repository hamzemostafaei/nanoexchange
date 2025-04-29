package dev.hamze.nanoexchange.common.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonSerializationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializationUtil.class);

    private static final ObjectMapper OBJECT_MAPPER;
    private static final ObjectMapper FLEXIBLE_OBJECT_MAPPER;
    private static final ObjectMapper PRETTY_PRINT_OBJECT_MAPPER;
    private static final Map<Class<?>, ObjectReader> OBJECT_READER_MAP;
    private static final Map<Class<?>, ObjectReader> FLEXIBLE_OBJECT_READER_MAP;
    private static final Map<Class<?>, ObjectWriter> OBJECT_WRITER_MAP;
    private static final Map<Class<?>, ObjectWriter> PRETTY_PRINT_OBJECT_WRITER_MAP;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);

        FLEXIBLE_OBJECT_MAPPER = new ObjectMapper();
        FLEXIBLE_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        FLEXIBLE_OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        FLEXIBLE_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        FLEXIBLE_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        FLEXIBLE_OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        FLEXIBLE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PRETTY_PRINT_OBJECT_MAPPER = new ObjectMapper();
        PRETTY_PRINT_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        PRETTY_PRINT_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        PRETTY_PRINT_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        PRETTY_PRINT_OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        PRETTY_PRINT_OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);

        OBJECT_READER_MAP = new HashMap<>();
        OBJECT_READER_MAP.put(null, OBJECT_MAPPER.readerFor(Object.class));

        FLEXIBLE_OBJECT_READER_MAP = new HashMap<>();
        FLEXIBLE_OBJECT_READER_MAP.put(null, OBJECT_MAPPER.readerFor(Object.class));

        OBJECT_WRITER_MAP = new HashMap<>();
        OBJECT_WRITER_MAP.put(null, OBJECT_MAPPER.writerFor(Object.class));

        PRETTY_PRINT_OBJECT_WRITER_MAP = new HashMap<>();
        PRETTY_PRINT_OBJECT_WRITER_MAP.put(null, PRETTY_PRINT_OBJECT_MAPPER.writerFor(Object.class));
    }

    public static String objectToJsonString(Object object) {
        try {
            Class<?> type = object == null ? Object.class : object.getClass();
            ObjectWriter objectWriter = getWriter(type);

            return objectWriter.writeValueAsString(object);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while serializing object to JSON", e);
            }

            return null;
        }
    }

    public static String objectToPrettyJsonString(Object object) {
        try {
            Class<?> type = object == null ? Object.class : object.getClass();
            ObjectWriter objectWriter = getPrettyPrintWriter(type);

            return objectWriter.writeValueAsString(object);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while serializing object to JSON", e);
            }

            return null;
        }
    }

    public static JsonNode objectToJsonNode(Object object) {
        try {
            return OBJECT_MAPPER.valueToTree(object);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while serializing object to JSON", e);
            }

            return null;
        }
    }

    public static <T> T jsonNodeToObject(JsonNode jsonNode, Class<T> type) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, type);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while Deserializing object from JSON", e);
            }

            return null;
        }
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> type) {
        try {
            ObjectReader objectReader = getReader(type);
            return objectReader.readValue(jsonString);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while Deserializing object from JSON", e);
            }

            return null;
        }
    }

    public static <T> T jsonStringToObject(String jsonString, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while Deserializing object from JSON", e);
            }

            return null;
        }
    }

    public static JsonNode jsonStringToJsonNode(String jsonString) {
        try {
            return OBJECT_MAPPER.readTree(jsonString);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error occurred while reading JSON string as tree", e);
            }

            return null;
        }
    }

    public static ObjectReader getReader(Class<?> type) {

        ObjectReader objectReader = OBJECT_READER_MAP.get(type);
        if (objectReader != null) {
            return objectReader;
        }

        synchronized (OBJECT_MAPPER) {
            objectReader = OBJECT_READER_MAP.get(type);
            if (objectReader != null) {
                return objectReader;
            }

            objectReader = OBJECT_MAPPER.readerFor(type);
            OBJECT_READER_MAP.put(type, objectReader);
        }

        return objectReader;
    }

    private static <T> ObjectReader getFlexibleReader(Class<T> type) {

        ObjectReader objectReader = FLEXIBLE_OBJECT_READER_MAP.get(type);
        if (objectReader != null) {
            return objectReader;
        }

        synchronized (FLEXIBLE_OBJECT_MAPPER) {
            objectReader = FLEXIBLE_OBJECT_READER_MAP.get(type);
            if (objectReader != null) {
                return objectReader;
            }

            objectReader = FLEXIBLE_OBJECT_MAPPER.readerFor(type);
            FLEXIBLE_OBJECT_READER_MAP.put(type, objectReader);
        }

        return objectReader;
    }

    public static ObjectWriter getWriter(Class<?> type) {

        ObjectWriter objectWriter = OBJECT_WRITER_MAP.get(type);
        if (objectWriter != null) {
            return objectWriter;
        }

        synchronized (OBJECT_MAPPER) {
            objectWriter = OBJECT_WRITER_MAP.get(type);
            if (objectWriter != null) {
                return objectWriter;
            }

            objectWriter = OBJECT_MAPPER.writerFor(type);
            OBJECT_WRITER_MAP.put(type, objectWriter);
        }

        return objectWriter;
    }

    public static ObjectWriter getPrettyPrintWriter(Class<?> type) {

        ObjectWriter objectWriter = PRETTY_PRINT_OBJECT_WRITER_MAP.get(type);
        if (objectWriter != null) {
            return objectWriter;
        }

        synchronized (PRETTY_PRINT_OBJECT_MAPPER) {
            objectWriter = PRETTY_PRINT_OBJECT_WRITER_MAP.get(type);
            if (objectWriter != null) {
                return objectWriter;
            }

            objectWriter = PRETTY_PRINT_OBJECT_MAPPER.writerFor(type);
            PRETTY_PRINT_OBJECT_WRITER_MAP.put(type, objectWriter);
        }

        return objectWriter;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static boolean isJsonObject(String jsonString) {
        ObjectMapper mapper = getObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            return jsonNode.isObject();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJsonArray(String jsonString) {
        JsonNode jsonNode = jsonStringToJsonNode(jsonString);
        if (jsonNode == null) {
            return false;
        }

        return jsonNode.isArray();
    }

    public static boolean isValidJson(String jsonString) {
        JsonNode jsonNode = jsonStringToJsonNode(jsonString);
        return jsonNode != null;
    }
}
