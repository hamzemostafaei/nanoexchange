package dev.hamze.nanoexchange.presentation.filters.body.extraction;

import jakarta.servlet.FilterChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public interface IExchangeBodyExtractionEventHandler {

    default void handleRequestBodyExtracted(ContentCachingRequestWrapper request,
                                            ContentCachingResponseWrapper response,
                                            FilterChain chain) {
    }

    default void handleResponseBodyExtracted(ContentCachingRequestWrapper request,
                                             ContentCachingResponseWrapper response,
                                             FilterChain chain,
                                             long startTime) {
    }
}
