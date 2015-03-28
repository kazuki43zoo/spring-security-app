package springsecurity.itest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;


@WebAppConfiguration
@ContextConfiguration(classes = {
        WebApplicationInitializer.WebApplicationConfig.class,
        WebSecurityConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class AnonymousUserSecurityTest {

    @Autowired
    MockHttpSession session;

    @Autowired
    MockHttpServletRequest request;

    @Autowired
    MockHttpServletResponse response;

    @Autowired
    FilterChainProxy filterChainProxy;

    MockFilterChain mockFilterChain;

    @Before
    public void setupMockFilterChain() {
        this.mockFilterChain = new MockFilterChain();
    }

    @Test
    public void accessTopPage() throws IOException, ServletException {
        request.setServletPath("/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url",
                response.getRedirectedUrl(), is("http://localhost/login?error=unauthorized"));
    }

    @Test
    public void accessLoginPage() throws IOException, ServletException {
        request.setServletPath("/login");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), notNullValue());
        assertThat(mockFilterChain.getResponse(), notNullValue());

    }

    @Test
    public void accessApi() throws IOException, ServletException {
        request.setServletPath("/api/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        assertThat("redirected url",
                response.getRedirectedUrl(), nullValue());
        assertThat("forwarded url",
                response.getForwardedUrl(), nullValue());
    }

    @Test
    public void accessTopPageWithInvalidSession() throws IOException, ServletException {

        request.setServletPath("/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        request.setRequestedSessionId("invalidSessionId");
        request.setRequestedSessionIdValid(false);
        request.setSession(null);

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url",
                response.getRedirectedUrl(), is("/login?invalidSession"));
    }

    @Test
    public void authenticateSuccess() throws IOException, ServletException {
        request.setServletPath("/account");
        request.setRequestURI("/account");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));

        response.setCommitted(false);
        response.reset();

        request.setServletPath("/authenticate");
        request.addParameter("username", "demo");
        request.addParameter("password", "demo");
        request.addParameter(csrfToken.getParameterName(), csrfToken.getToken());
        request.setMethod(HttpMethod.POST.name());

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        MockHttpSession newMockHttpSession = MockHttpSession.class.cast(request.getSession());
        CsrfToken newCsrfToken = CsrfToken.class.cast(
                newMockHttpSession.getAttribute(HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN")));

        assertTrue("session status (before authentication):", session.isInvalid());
        assertFalse("session status (after authentication):", newMockHttpSession.isInvalid());

        assertThat("session id:",
                newMockHttpSession.getId(), not(is(session.getId())));
        assertThat("csrf token:",
                newCsrfToken.getToken(), not(is(csrfToken.getToken())));

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url",
                response.getRedirectedUrl(), is("http://localhost/account"));
    }

    @Test
    public void authenticateFailure() throws IOException, ServletException {
        request.setServletPath("/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));

        response.setCommitted(false);
        response.reset();

        request.setServletPath("/authenticate");
        request.addParameter("username", "demo");
        request.addParameter("password", "xxxx");
        request.addParameter(csrfToken.getParameterName(), csrfToken.getToken());
        request.setMethod(HttpMethod.POST.name());

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat("redirect url",
                response.getForwardedUrl(), is("/login?error=failed"));
    }

}
