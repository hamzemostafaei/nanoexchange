package dev.hamze.nanoexchange.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "dev.hamze.nanoexchange.logging")
public record LoggingConfigData(List<String> nonLoggedPaths,List<String> ignoredHeaders) {
}
