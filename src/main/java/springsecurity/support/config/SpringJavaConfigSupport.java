package springsecurity.support.config;

import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SpringJavaConfigSupport {

    private static PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private SpringJavaConfigSupport() {
    }

    public static Resource[] resources(String... locationPatterns) throws IOException {
        List<Resource> resources = new ArrayList<>();
        for (String locationPattern : locationPatterns) {
            resources.addAll(Arrays.asList(resourcePatternResolver.getResources(locationPattern)));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Configuration
    public static class PropertyConfig {

        @Bean
        static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setLocations(propertyResources());
            return configurer;
        }

        @Bean
        static PropertyOverrideConfigurer propertyOverrideConfigurer() throws IOException {
            PropertyOverrideConfigurer configurer = new PropertyOverrideConfigurer();
            configurer.setIgnoreInvalidKeys(true);
            configurer.setLocations(propertyResources());
            return configurer;
        }

        @Bean
        static Resource[] propertyResources() throws IOException {
            return resources(
                    "classpath*:/config/default/**/*.properties",
                    "classpath*:/config/" + System.getProperty("app.env", "local") + "/**/*.properties"
            );
        }

    }

}
