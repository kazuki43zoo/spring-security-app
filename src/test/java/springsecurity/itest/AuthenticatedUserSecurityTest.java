package springsecurity.itest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;
import springsecurity.app.AppServletInitializer;
import springsecurity.domain.model.Account;

import javax.servlet.*;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;


@ContextHierarchy({
        @ContextConfiguration(classes = {
                WebApplicationInitializer.WebApplicationConfig.class,
                WebSecurityConfig.class
        }),
        @ContextConfiguration(classes = AppServletInitializer.AppServletConfig.class)
})
public class AuthenticatedUserSecurityTest extends ITestSupport {

    @Before
    public void doLogin() throws IOException, ServletException {
        login().username("demo").password("demo").perform();
    }

    @Test
    public void accessTopPage() throws IOException, ServletException {
        get("/").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.OK.value()));
        assertThat("forwarded url:",
                response.getForwardedUrl(), is(jspPath("welcome/home")));
    }


    @Test
    public void accessLoginPage() throws IOException, ServletException {
        get("/login").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void accessAllowedResource() throws IOException, ServletException {
        get("/account").perform();

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.OK.value()));
        assertThat("forwarded url:",
                response.getForwardedUrl(), is(jspPath("account/detail")));

        Account account = Account.class.cast(request.getAttribute("account"));
        assertThat(account.getUsername(), is("demo"));
    }

    @Test
    public void accessNotAllowedResource() throws IOException, ServletException {
        get("/admin").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void logout() throws IOException, ServletException {
        post("/logout").perform();

        assertThat("http status code:",
                response.getStatus(), is(HttpStatus.FOUND.value()));
        assertThat("redirect url:",
                response.getRedirectedUrl(), is("/login?logout"));

        assertThat(request.getSession(false), nullValue());
        assertThat(response.getCookie("JSESSIONID").getValue(), nullValue());
    }

}
