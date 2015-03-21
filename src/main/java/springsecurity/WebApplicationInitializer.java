package springsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;
import springsecurity.domain.DomainConfig;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class WebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    public static final String DEFAULT_CHARACTER_ENCODING = StandardCharsets.UTF_8.name();

    public WebApplicationInitializer() {
        super(WebApplicationConfig.class, WebSecurityConfig.class);
    }

    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        insertFilters(servletContext, characterEncodingFilter());
    }

    @Override
    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
        servletContext.getFilterRegistration(DEFAULT_FILTER_NAME)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD), true, "/authenticate");
    }

    private CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(DEFAULT_CHARACTER_ENCODING);
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Configuration
    @Import({EnvConfigs.class, DomainConfig.class})
    static class WebApplicationConfig {

    }

}
