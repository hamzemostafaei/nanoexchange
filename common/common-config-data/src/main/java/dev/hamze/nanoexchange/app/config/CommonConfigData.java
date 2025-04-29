package dev.hamze.nanoexchange.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "dev.hamze.nanoexchange")
public record CommonConfigData(Integer nodeId,
                               List<String> ignoredPathPatterns,
                               String serviceName) {
}
