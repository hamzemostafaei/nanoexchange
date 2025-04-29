package dev.hamze.nanoexchange.presentation.filters.logging;

import dev.hamze.nanoexchange.app.config.CommonConfigData;
import dev.hamze.nanoexchange.common.utility.LoggingUtil;
import dev.hamze.nanoexchange.common.utility.SnowFlakeUniqueIDGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static dev.hamze.nanoexchange.presentation.WebApplicationConstants.CORRELATION_ID_HEADER_KEY;
import static dev.hamze.nanoexchange.presentation.WebApplicationConstants.REQUEST_ID_HEADER_KEY;


@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class ServiceLoggingFilter implements Filter {

    private final CommonConfigData commonConfigData;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestId = null;
        try {
            if (request instanceof HttpServletRequest httpRequest) {
                String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER_KEY);
                if (StringUtils.isBlank(correlationId)) {
                    correlationId = UUID.randomUUID().toString();
                }
                requestId = httpRequest.getHeader(REQUEST_ID_HEADER_KEY);
                LoggingUtil.setCorrelationIdToContext(correlationId);
            }

            LoggingUtil.setNodeIdToContext(commonConfigData.nodeId().toString());

            if (requestId == null) {
                requestId = Long.toString(SnowFlakeUniqueIDGenerator.nextId(commonConfigData.nodeId()));
            }
            LoggingUtil.setRequestIdToContext(requestId);
            LoggingUtil.setRequestDateToContext(new Date());
        } catch (Exception ex) {
            if (log.isWarnEnabled()) {
                String message = "Unable to set logging context parameters";
                log.warn(message, ex);
            }
        }

        chain.doFilter(request, response);
    }
}
