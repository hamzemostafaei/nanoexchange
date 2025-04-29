package dev.hamze.nanoexchange.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dev.hamze.nanoexchange.search")
public record SearchCommonConfigData(Integer pageSize) {
}
