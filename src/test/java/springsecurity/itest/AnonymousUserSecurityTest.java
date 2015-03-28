package springsecurity.itest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;


@ContextConfiguration(classes = {
    WebApplicationInitializer.WebApplicationConfig.class,
    WebSecurityConfig.class
})
public class AnonymousUserSecurityTest extends ITestSupport {

    @Test
    public void accessTopPage() throws IOException, ServletException {

        get("/").perform();

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("http://localhost/login?error=unauthorized"));
    }

    @Test
    public void accessLoginPage() throws IOException, ServletException {

        get("/login").perform();

        assertThat(mockFilterChain.getRequest(), notNullValue());
        assertThat(mockFilterChain.getResponse(), notNullValue());

    }

    @Test
    public void accessApi() throws IOException, ServletException {

        get("/api").perform();

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        assertThat("redirected url:",
                response.getRedirectedUrl(), nullValue());
        assertThat("forwarded url:",
                response.getForwardedUrl(), nullValue());
    }

    @Test
    public void accessTopPageWithInvalidSession() throws IOException, ServletException {

        request.setRequestedSessionId("invalidSessionId");
        request.setRequestedSessionIdValid(false);
        request.setSession(null);

        get("/").perform();

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("/login?invalidSession"));
    }

    @Test
    public void authenticateSuccess() throws IOException, ServletException {
        get("/account").perform();

        assertThat(mockFilterChain.getRequest(), nullValue());
        assertThat(mockFilterChain.getResponse(), nullValue());

        CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));

        authentication()
                .username("demo").password("demo").csrfToken(csrfToken)
                .perform();

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

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("http://localhost/account"));
    }

    @Test
    public void authenticateFailureCauseByUserNotFound() throws IOException, ServletException {

        CsrfToken csrfToken = getCsrfToken();

        authentication()
                .username("unknownUser").password("demo").csrfToken(csrfToken)
                .perform();

        assertThat("redirect url",
                response.getForwardedUrl(), is("/login?error=failed"));
    }

    @Test
    public void authenticateFailureCauseByPasswordMismatch() throws IOException, ServletException {
        CsrfToken csrfToken = getCsrfToken();

        authentication()
                .username("demo").password("xxxx").csrfToken(csrfToken)
                .perform();

        assertThat("redirect url",
                response.getForwardedUrl(), is("/login?error=failed"));
    }


}
