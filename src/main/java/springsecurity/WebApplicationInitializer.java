package springsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import springsecurity.domain.DomainConfig;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class WebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    public static final Charset DEFAULT_CHARACTER_ENCODING = StandardCharsets.UTF_8;

    public WebApplicationInitializer() {
        super(WebApplicationConfig.class, WebSecurityConfig.class);
    }

    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        insertFilters(servletContext,
                characterEncoding(),
                resourceUrlEncoding());
    }

    @Override
    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
        servletContext.getFilterRegistration(DEFAULT_FILTER_NAME)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD), true, "/authenticate");
    }

    private static CharacterEncodingFilter characterEncoding() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(DEFAULT_CHARACTER_ENCODING.name());
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    private static ResourceUrlEncodingFilter resourceUrlEncoding() {
        return new ResourceUrlEncodingFilter();
    }

    @Configuration
    @Import({EnvConfigs.class, DomainConfig.class})
    static class WebApplicationConfig {

    }

}
