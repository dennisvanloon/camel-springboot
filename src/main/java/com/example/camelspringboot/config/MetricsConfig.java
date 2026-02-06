package com.example.camelspringboot.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MicrometerRoutePolicyFactory micrometerRoutePolicyFactory(MeterRegistry meterRegistry) {
        MicrometerRoutePolicyFactory factory = new MicrometerRoutePolicyFactory();
        factory.setMeterRegistry(meterRegistry);
        return factory;
    }
}