package dev.hamze.nanoexchange.common.bootstrap;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = "dev.hamze.nanoexchange")
public class BootstrapConfiguration {
}
