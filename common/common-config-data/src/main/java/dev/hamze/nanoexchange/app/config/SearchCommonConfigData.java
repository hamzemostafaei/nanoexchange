package dev.hamze.nanoexchange.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dev.microservices.lab.search")
public record SearchCommonConfigData(Integer pageSize) {
}
