package dev.hamze.nanoexchange.presentation.filters.logging;

import dev.microservices.lab.app.config.CommonConfigData;
import dev.microservices.lab.app.config.LoggingConfigData;
import dev.microservices.lab.common.utility.LoggingUtil;
import dev.microservices.lab.common.utility.WebUtil;
import dev.microservices.lab.presentation.filters.body.extraction.IExchangeBodyExtractionEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingExchangeBodyExtractionEventHandler implements IExchangeBodyExtractionEventHandler {

    private static final long NANO_TO_MILLI_DIVIDER = (long) Math.pow(10, 6);

    private final PathMatcher pathMatcher;
    private final CommonConfigData commonConfigData;
    private final LoggingConfigData loggingConfigData;

    private Logger serviceNameLogger;

    private static void setEffectiveUserNameToContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails user) {
                LoggingUtil.setUserNameToContext(user.getUsername());
            }
        }
    }

    @PostConstruct
    public void init() {
        serviceNameLogger = LoggerFactory.getLogger(commonConfigData.serviceName());
    }

    @Override
    public void handleRequestBodyExtracted(ContentCachingRequestWrapper request,
                                           ContentCachingResponseWrapper response,
                                           FilterChain filterChain) {

        setEffectiveUserNameToContext();

        String path = WebUtil.getEffectivePath(request);
        boolean ignored = WebUtil.pathMatches(path, loggingConfigData.nonLoggedPaths(), pathMatcher);
        if (!ignored) {
            String query = request.getQueryString();
            String method = request.getMethod();
            MultiValueMap<String, String> requestHeaders = WebUtil.getEffectiveHeaders(request, loggingConfigData.ignoredHeaders());
            String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);

            if (log.isInfoEnabled()) {
                log.info(
                        "REQUEST-RECEIVED: {}",
                        path + (StringUtils.hasText(query) ? query : ""));
            }

            if (log.isDebugEnabled()) {
                log.debug(
                        "REQUEST-SPEC: {} {}\n {}\n {}",
                        method,
                        path + (StringUtils.hasText(query) ? query : ""),
                        requestHeaders,
                        requestBody);
            }

            if (serviceNameLogger.isInfoEnabled()) {
                serviceNameLogger.info(
                        "REQUEST: {}",
                        WebUtil.getRequestLogMessage(method, path, query, requestHeaders, requestBody));
            }
        }
    }

    @Override
    public void handleResponseBodyExtracted(ContentCachingRequestWrapper request,
                                            ContentCachingResponseWrapper response,
                                            FilterChain filterChain,
                                            long startTime) {

        String path = WebUtil.getEffectivePath(request);
        boolean ignored = WebUtil.pathMatches(path, loggingConfigData.nonLoggedPaths(), pathMatcher);
        if (!ignored) {
            MultiValueMap<String, String> responseHeaders = WebUtil.getEffectiveHeaders(response, loggingConfigData.ignoredHeaders());
            String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

            if (log.isDebugEnabled()) {
                log.debug("RESPONSE-HEADERS: {}", responseHeaders);
                log.debug("RESPONSE-BODY: {}", responseBody);
            }

            if (log.isInfoEnabled()) {
                long elapsed = (System.nanoTime() - startTime) / NANO_TO_MILLI_DIVIDER;
                log.info("RESPONSE-TIME: {}", elapsed);
            }

            if (serviceNameLogger.isInfoEnabled()) {
                serviceNameLogger.info(
                        "RESPONSE: {}",
                        WebUtil.getResponseLogMessage(response.getStatus(), responseHeaders, responseBody));
            }
        }

        try {
            LoggingUtil.removeCorrelationIdFromContext();
            LoggingUtil.removeNodeIdFromContext();
            LoggingUtil.removeRequestIdFromContext();
            LoggingUtil.removeUserNameFromContext();
            LoggingUtil.removeRequestDateFromContext();
        } catch (Exception ex) {
            if (log.isWarnEnabled()) {
                String message = "Unable to remove logging context parameters";
                log.warn(message, ex);
            }
        }
    }
}
