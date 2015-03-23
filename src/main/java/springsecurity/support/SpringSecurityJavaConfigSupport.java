package springsecurity.support;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    public static AuthenticationEntryPoint sendErrorEntryPoint(final HttpStatus httpStatus) {
        return new AuthenticationEntryPoint() {
            public void commence(HttpServletRequest request, HttpServletResponse
                    response, AuthenticationException authException)
                    throws IOException, ServletException {
                response.sendError(httpStatus.value());
            }
        };
    }

    public static AuthenticationFailureHandler failureUrl(String failureUrl, boolean useForward) {
        SimpleUrlAuthenticationFailureHandler failureHandler =
                new SimpleUrlAuthenticationFailureHandler(failureUrl);
        failureHandler.setUseForward(useForward);
        return failureHandler;
    }

}
