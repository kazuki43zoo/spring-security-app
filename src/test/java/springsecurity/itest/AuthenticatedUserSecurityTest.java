package springsecurity.itest;

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

    @Test
    public void accessTopPage() throws IOException, ServletException {

        login()
                .username("demo").password("demo")
                .perform();

        request.setServletPath("/");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.OK.value()));
        assertThat("forwarded url:",
                response.getForwardedUrl(), is(jspPath("welcome/home")));

    }


    @Test
    public void accessLoginPage() throws IOException, ServletException {

        login()
                .username("demo").password("demo")
                .perform();

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

        login()
                .username("demo").password("demo")
                .perform();

        request.setServletPath("/account");
        request.setMethod(HttpMethod.GET.name());
        request.addHeader("Accept", "text/html");

        filterChainProxy.doFilter(request, response, mockFilterChain);

        assertThat("http status code : ",
                response.getStatus(), is(HttpStatus.OK.value()));
        assertThat("forwarded url:",
                response.getForwardedUrl(), is(jspPath("account/detail")));

        Account account = Account.class.cast(request.getAttribute("account"));
        assertThat(account.getUsername(), is("demo"));

    }

    @Test
    public void accessNotAllowedResource() throws IOException, ServletException {

        login()
                .username("demo").password("demo")
                .perform();

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

        login()
                .username("demo").password("demo")
                .perform();

        CsrfToken csrfToken = loadCsrfToken();

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


}
