package com.jiucom.api.global.actuator;

import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomMetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "jiucom-api");
    }

    @Bean
    public Gauge totalUsersGauge(MeterRegistry registry, UserRepository userRepository) {
        return Gauge.builder("jiucom.users.total", userRepository::count)
                .description("Total registered users")
                .register(registry);
    }

    @Bean
    public Gauge totalPartsGauge(MeterRegistry registry, PartRepository partRepository) {
        return Gauge.builder("jiucom.parts.total", partRepository::count)
                .description("Total parts in database")
                .register(registry);
    }

    @Bean
    public Gauge totalPostsGauge(MeterRegistry registry, PostRepository postRepository) {
        return Gauge.builder("jiucom.posts.total", postRepository::count)
                .description("Total posts in database")
                .register(registry);
    }
}
