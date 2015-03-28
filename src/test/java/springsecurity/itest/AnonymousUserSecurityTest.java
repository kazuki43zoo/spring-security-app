package springsecurity.itest;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.validation.BindingResult;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;
import springsecurity.app.AppServletInitializer;
import springsecurity.app.auth.LoginForm;
import springsecurity.core.setting.SecuritySetting;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;


@ContextHierarchy({
        @ContextConfiguration(classes = {
                WebApplicationInitializer.WebApplicationConfig.class,
                WebSecurityConfig.class
        }),
        @ContextConfiguration(classes = AppServletInitializer.AppServletConfig.class)
})
public class AnonymousUserSecurityTest extends ITestSupport {

    @Test
    public void accessTopPage() throws IOException, ServletException {
        get("/").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("http://localhost/login?error=unauthorized"));
    }

    @Test
    public void accessLoginPage() throws IOException, ServletException {
        get("/login").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.OK.value()));
        assertThat("forwarded url:",
                response.getForwardedUrl(), is(jspPath("auth/login")));

        LoginForm loginForm = LoginForm.class.cast(request.getAttribute("loginForm"));
        SecuritySetting securitySetting = SecuritySetting.class.cast(request.getAttribute("securitySetting"));
        assertThat(loginForm.getUsername(), is(securitySetting.getDemoUsername()));
        assertThat(loginForm.getPassword(), is(securitySetting.getDemoPassword()));
    }

    @Test
    public void accessApi() throws IOException, ServletException {
        get("/api").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        assertThat("redirected url:",
                response.getRedirectedUrl(), nullValue());
        assertThat("forwarded url:",
                response.getForwardedUrl(), nullValue());
    }

    @Test
    public void accessTopPageWithInvalidSession() throws IOException, ServletException {
        invalidSession();

        get("/").reset(false).perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("/login?invalidSession"));
    }

    @Test
    public void loginInputCheckIsOk() throws IOException, ServletException {
        authentication().path("/login").username("demo").password("demo").csrfToken(getCsrfToken()).perform();

        assertThat("forwarded url:",
                response.getForwardedUrl(), is("/authenticate"));
    }

    @Test
    public void authenticateSuccess() throws IOException, ServletException {
        get("/account").perform();

        CsrfToken csrfToken = loadCsrfToken();

        authentication().username("demo").password("demo").perform();

        MockHttpSession newSession = MockHttpSession.class.cast(request.getSession());
        CsrfToken newCsrfToken = loadCsrfToken();

        assertTrue("session status (before authentication):", session.isInvalid());
        assertFalse("session status (after authentication):", newSession.isInvalid());
        assertThat("session id:",
                newSession.getId(), not(is(session.getId())));
        assertThat("csrf token:",
                newCsrfToken.getToken(), not(is(csrfToken.getToken())));

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("http://localhost/account"));
    }

    @Test
    public void authenticateFailureCauseByCredentialIsEmpty() throws IOException, ServletException {
        authentication().path("/login").username("").password("").csrfToken(getCsrfToken()).perform();

        assertThat("redirect url:",
                response.getForwardedUrl(), is(jspPath("auth/login")));

        BindingResult result = getBindingResult("loginForm");
        assertThat("binding error count:",
                result.getErrorCount(), is(2));
        assertTrue("binding error username:",
                Arrays.asList(result.getFieldError("username").getCodes()).contains("NotNull"));
        assertTrue("binding error password:",
                Arrays.asList(result.getFieldError("password").getCodes()).contains("NotNull"));

        LoginForm loginForm = LoginForm.class.cast(request.getAttribute("loginForm"));
        assertThat(loginForm.getUsername(), nullValue());
        assertThat(loginForm.getPassword(), nullValue());
    }

    @Test
    public void authenticateFailureCauseByUserNotFound() throws IOException, ServletException {
        authentication()
                .username("unknownUser").password("demo").csrfToken(getCsrfToken())
                .perform();

        assertThat("forwarded url:",
                response.getForwardedUrl(), is("/login?error=failed"));
    }

    @Test
    public void authenticateFailureCauseByPasswordMismatch() throws IOException, ServletException {
        authentication()
                .username("demo").password("xxxx").csrfToken(getCsrfToken())
                .perform();

        assertThat("forwarded url:",
                response.getForwardedUrl(), is("/login?error=failed"));
    }

}
