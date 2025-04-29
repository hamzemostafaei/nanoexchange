package dev.hamze.nanoexchange.common.utility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;

import java.util.*;

public final class WebUtil {

    public static String getEffectivePath(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String path = request.getRequestURI();

        return path.replace(request.getContextPath(), "");
    }

    public static boolean pathMatches(String path, List<String> pathPatterns) {
        PathMatcher pathMatcher = ApplicationContextUtil.getBean(PathMatcher.class);
        return pathMatches(path, pathPatterns, pathMatcher);
    }

    public static boolean pathMatches(String path, List<String> pathPatterns, PathMatcher pathMatcher) {

        if (CollectionUtils.isEmpty(pathPatterns)) {
            return false;
        }

        for (String pathPattern : pathPatterns) {
            if (pathMatcher.match(pathPattern, path)) {
                return true;
            }
        }

        return false;
    }

    public static MultiValueMap<String, String> getEffectiveHeaders(HttpServletRequest httpRequest,
                                                                    List<String> ignoredHeaders) {

        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();

        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> headerValues = Collections.list(httpRequest.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }

        return getEffectiveHeaders(headerMap, ignoredHeaders);
    }

    public static MultiValueMap<String, String> getEffectiveHeaders(HttpServletResponse httpResponse,
                                                                    List<String> ignoredHeaders) {

        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();

        Collection<String> headerNames = httpResponse.getHeaderNames();
        for (String headerName : headerNames) {
            List<String> headerValues = new ArrayList<>(httpResponse.getHeaders(headerName));
            headerMap.put(headerName, headerValues);
        }

        return getEffectiveHeaders(headerMap, ignoredHeaders);
    }

    public static MultiValueMap<String, String> getEffectiveHeaders(MultiValueMap<String, String> headerMap,
                                                                    List<String> ignoredHeaders) {

        MultiValueMap<String, String> effectiveHeaderMap = new LinkedMultiValueMap<>(headerMap);

        for (String nonLoggedHeader : ignoredHeaders) {
            effectiveHeaderMap.remove(nonLoggedHeader);
            effectiveHeaderMap.remove(nonLoggedHeader.toLowerCase());
        }

        return effectiveHeaderMap;
    }

    public static String getRequestLogMessage(HttpMethod httpMethod,
                                              String baseUrl,
                                              String path,
                                              MultiValueMap<String, String> headers,
                                              Object requestPayload,
                                              String trace) {

        String httpMethodName = null;
        if (httpMethod != null) {
            httpMethodName = httpMethod.name();
        }

        String serializedPayload = getSerializedPayload(requestPayload);

        return getRequestLogMessage(httpMethodName, baseUrl, path, null, headers, serializedPayload, trace);
    }

    public static String getRequestLogMessage(String httpMethod,
                                              String path,
                                              String query,
                                              MultiValueMap<String, String> headers,
                                              String requestPayload) {

        return getRequestLogMessage(httpMethod, null, path, query, headers, requestPayload, null);
    }

    public static String getRequestLogMessage(String httpMethod,
                                              String baseUrl,
                                              String path,
                                              String query,
                                              MultiValueMap<String, String> headers,
                                              String requestPayload,
                                              String trace) {

        Map<String, Object> requestInfoMap = new HashMap<>(5);
        requestInfoMap.put("httpMethod", httpMethod);
        if (baseUrl != null) {
            requestInfoMap.put("baseUrl", baseUrl);
        }
        requestInfoMap.put("path", path);
        requestInfoMap.put("query", query);
        requestInfoMap.put("requestHeaders", headers);
        requestInfoMap.put("requestPayload", requestPayload);
        if (trace != null) {
            requestInfoMap.put("trace", trace);
        }

        return JsonSerializationUtil.objectToJsonString(requestInfoMap);
    }

    public static String getSerializedPayload(Object payload) {

        if (payload == null) {
            return null;
        } else if (payload instanceof String) {
            return  (String) payload;
        } else {
            return JsonSerializationUtil.objectToJsonString(payload);
        }
    }

    public static String getResponseLogMessage(Integer statusCode,
                                               MultiValueMap<String, String> headers,
                                               String responsePayload) {

        return getResponseLogMessage(statusCode, headers, responsePayload, null);
    }

    public static String getResponseLogMessage(HttpStatusCode responseStatus,
                                               MultiValueMap<String, String> headers,
                                               Object responsePayload,
                                               String trace) {

        Integer intStatusCode = null;
        if (responseStatus != null) {
            intStatusCode = responseStatus.value();
        }

        String serializedPayload = getSerializedPayload(responsePayload);

        return getResponseLogMessage(intStatusCode, headers, serializedPayload, trace);
    }

    public static String getResponseLogMessage(Integer statusCode,
                                               MultiValueMap<String, String> headers,
                                               String responsePayload,
                                               String trace) {

        Map<String, Object> responseInfoMap = new HashMap<>(3);

        responseInfoMap.put("statusCode", Objects.toString(statusCode));
        responseInfoMap.put("responseHeaders", headers);
        responseInfoMap.put("responsePayload", responsePayload);
        if (trace != null) {
            responseInfoMap.put("trace", trace);
        }

        return JsonSerializationUtil.objectToJsonString(responseInfoMap);
    }
}
