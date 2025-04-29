package dev.hamze.nanoexchange.common.utility;

import org.slf4j.MDC;

import java.util.Date;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LoggingUtil {

    public static final String CORRELATION_ID_CONTEXT_KEY = "CORRELATION-ID";
    public static final String REQUEST_ID_CONTEXT_KEY = "RQST-ID";
    public static final String REQUEST_DATE_CONTEXT_KEY = "RQST-DATE";
    public static final String NODE_ID_CONTEXT_KEY = "NODE-ID";
    public static final String USER_NAME_CONTEXT_KEY = "USR-ID";

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static String getFromContext(String key) {
        return MDC.get(key);
    }

    public static void setToContext(String key, String value) {
        MDC.put(key, value);
    }

    public static String removeFromContext(String key) {
        String value = getFromContext(key);
        MDC.remove(key);

        return value;
    }

    public static String getRequestIdFromContext() {
        return getFromContext(REQUEST_ID_CONTEXT_KEY);
    }

    public static String getCorrelationIdContext() {
        return getFromContext(CORRELATION_ID_CONTEXT_KEY);
    }

    public static void setRequestIdToContext(String requestId) {
        setToContext(REQUEST_ID_CONTEXT_KEY, requestId);
    }

    public static void setCorrelationIdToContext(String correlationId) {
        setToContext(CORRELATION_ID_CONTEXT_KEY, correlationId);
    }

    public static String removeRequestIdFromContext() {
        return removeFromContext(REQUEST_ID_CONTEXT_KEY);
    }

    public static String removeCorrelationIdFromContext() {
        return removeFromContext(CORRELATION_ID_CONTEXT_KEY);
    }

    public static Date getRequestDateFromContext() {
        String timestampString = getFromContext(REQUEST_DATE_CONTEXT_KEY);
        if (timestampString == null) {
            return null;
        }

        return new Date(Long.parseLong(timestampString));
    }

    public static void setRequestDateToContext(Date requestDate) {
        String timestampString = Long.toString(requestDate.getTime());
        setToContext(REQUEST_DATE_CONTEXT_KEY, timestampString);
    }

    public static Date removeRequestDateFromContext() {
        String timestampString = removeFromContext(REQUEST_DATE_CONTEXT_KEY);
        if (timestampString == null) {
            return null;
        }

        return new Date(Long.parseLong(timestampString));
    }

    public static String getNodeIdFromContext() {
        return getFromContext(NODE_ID_CONTEXT_KEY);
    }

    public static void setNodeIdToContext(String nodeId) {
        setToContext(NODE_ID_CONTEXT_KEY, nodeId);
    }

    public static String removeNodeIdFromContext() {
        return removeFromContext(NODE_ID_CONTEXT_KEY);
    }

    public static String getUserNameFromContext() {
        return getFromContext(USER_NAME_CONTEXT_KEY);
    }

    public static void setUserNameToContext(String userName) {
        setToContext(USER_NAME_CONTEXT_KEY, userName);
    }

    public static String removeUserNameFromContext() {
        return removeFromContext(USER_NAME_CONTEXT_KEY);
    }

    public static void clearContext() {
        MDC.clear();
    }
}
