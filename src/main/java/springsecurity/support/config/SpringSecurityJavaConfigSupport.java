package springsecurity.support.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

public final class SpringSecurityJavaConfigSupport {

    private SpringSecurityJavaConfigSupport() {
    }

    public static RequestMatcher antRequestMatcher(String... paths) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        for (String path : paths) {
            requestMatchers.add(new AntPathRequestMatcher(path));
        }
        return new OrRequestMatcher(requestMatchers);
    }

    public static AuthenticationEntryPoint sendErrorEntryPoint(HttpStatus httpStatus) {
        return (request, response, authException) -> {
            response.sendError(httpStatus.value());
        };
    }

    public static AuthenticationFailureHandler failureUrl(String failureUrl, boolean useForward) {
        SimpleUrlAuthenticationFailureHandler failureHandler =
                new SimpleUrlAuthenticationFailureHandler(failureUrl);
        failureHandler.setUseForward(useForward);
        return failureHandler;
    }

}
