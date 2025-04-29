package dev.hamze.nanoexchange.presentation.filters.body.extraction;

import dev.microservices.lab.app.config.CommonConfigData;
import dev.microservices.lab.common.utility.LoggingUtil;
import dev.microservices.lab.common.utility.ReflectionUtil;
import dev.microservices.lab.common.utility.WebUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;

import static dev.microservices.lab.presentation.WebApplicationConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE - 2)
public class BodyExtractionHTTPFilter implements Filter {

    private final CommonConfigData commonConfigData;

    private final PathMatcher pathMatcher;

    private final List<IExchangeBodyExtractionEventHandler> eventHandlers;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest &&
            response instanceof HttpServletResponse httpResponse) {

            long startTime = System.nanoTime();

            String path = WebUtil.getEffectivePath(httpRequest);

            ServletRequest effectiveRequest;
            ServletResponse effectiveResponse;
            ContentCachingRequestWrapper wrappedRequest = null;
            ContentCachingResponseWrapper wrappedResponse = null;
            if (WebUtil.pathMatches(path, commonConfigData.ignoredPathPatterns(), pathMatcher)) {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "HTTP request body on path [{}] is set not to be extracted", path);
                }

                effectiveRequest = httpRequest;
                effectiveResponse = httpResponse;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "HTTP request body on path [{}] is extracted", path);
                }

                wrappedRequest = new BodyExtractionRequestWrapper(httpRequest);
                wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

                effectiveRequest = wrappedRequest;
                effectiveResponse = wrappedResponse;

                if (eventHandlers != null) {
                    for (IExchangeBodyExtractionEventHandler eventHandler : eventHandlers) {
                        eventHandler.handleRequestBodyExtracted(wrappedRequest, wrappedResponse, chain);
                    }
                }
            }

            chain.doFilter(effectiveRequest, effectiveResponse);

            if (wrappedResponse != null) {
                wrappedResponse.addHeader(CORRELATION_ID_HEADER_KEY,LoggingUtil.getCorrelationIdContext());
                wrappedResponse.addHeader(REQUEST_ID_HEADER_KEY,LoggingUtil.getRequestIdFromContext());
                wrappedResponse.addHeader(REQUEST_DATE_HEADER_KEY, String.valueOf(LoggingUtil.getRequestDateFromContext()));
                if (eventHandlers != null) {
                    for (IExchangeBodyExtractionEventHandler eventHandler : eventHandlers) {
                        eventHandler.handleResponseBodyExtracted(wrappedRequest, wrappedResponse, chain, startTime);
                    }
                }

                wrappedResponse.copyBodyToResponse();
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Non HTTP servlet request/response of type [{}/{}] is received, request body cannot be extracted",
                        ReflectionUtil.getSimpleClassName(request),
                        ReflectionUtil.getSimpleClassName(response));
            }

            chain.doFilter(request, response);
        }
    }
}
