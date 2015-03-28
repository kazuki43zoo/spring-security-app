package springsecurity.itest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;


@ContextConfiguration(classes = {
        WebApplicationInitializer.WebApplicationConfig.class,
        WebSecurityConfig.class
})
public class AuthenticatedUserSecurityTest extends ITestSupport {


    @Autowired
    FilterChainProxy filterChainProxy;


    @Test
    public void accessTopPage() throws IOException, ServletException {

        authenticate();

        request.setServletPath("/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), notNullValue());
        assertThat(mockFilterChain.getResponse(), notNullValue());

    }


    @Test
    public void accessLoginPage() throws IOException, ServletException {

        authenticate();

        request.setServletPath("/login");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FORBIDDEN.value()));


    }

    @Test
    public void accessAllowedResource() throws IOException, ServletException {

        authenticate();

        request.setServletPath("/account");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), notNullValue());
        assertThat(mockFilterChain.getResponse(), notNullValue());

    }

    @Test
    public void accessNotAllowedResource() throws IOException, ServletException {

        authenticate();

        request.setServletPath("/admin/");
        request.setRequestURI("/admin/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FORBIDDEN.value()));

    }

    @Test
    public void logout() throws IOException, ServletException {

        authenticate();

        CsrfToken csrfToken = CsrfToken.class.cast(
                request.getSession(false).getAttribute(HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN")));

        request.setServletPath("/logout");
        request.setRequestURI("/logout");
        request.setMethod(HttpMethod.POST.name());
        request.addParameter(csrfToken.getParameterName(), csrfToken.getToken());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url",
                response.getRedirectedUrl(), is("/login?logout"));

        assertThat(request.getSession(false), nullValue());
        assertThat(response.getCookie("JSESSIONID").getValue(), nullValue());

    }

    private void authenticate() throws IOException, ServletException {

        reset();

        request.setServletPath("/account");
        request.setRequestURI("/account");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));

        reset();

        request.setServletPath("/authenticate");
        request.addParameter("username", "demo");
        request.addParameter("password", "demo");
        request.addParameter(csrfToken.getParameterName(), csrfToken.getToken());
        request.setMethod(HttpMethod.POST.name());

        filterChainProxy.doFilter(request, response, mockFilterChain);

        reset();
    }

}
