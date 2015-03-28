package springsecurity.itest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;


@WebAppConfiguration
@ContextConfiguration(classes = {
        WebApplicationInitializer.WebApplicationConfig.class,
        WebSecurityConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticatedUserSecurityTest {

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

    private void reset(){
        response.setCommitted(false);
        response.reset();
        mockFilterChain.reset();
        ServletContext servletContext = request.getServletContext();
        HttpSession httpSession = request.getSession(false);
        request = new MockHttpServletRequest(servletContext);
        request.setSession(httpSession);
    }

}
