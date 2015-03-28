package springsecurity.itest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebAppConfiguration
@ContextConfiguration(classes = {
        ITestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class ITestSupport {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockServletContext servletContext;

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
    public void setupMockFilterChain() throws ServletException {
        this.mockFilterChain = createMockFilterChain();
    }

    protected MockFilterChain createMockFilterChain() throws ServletException {
        DispatcherServlet servlet = new DispatcherServlet(wac);
        servlet.init(new MockServletConfig("appServlet"));
        return new MockFilterChain(servlet);
    }

    protected void reset() {
        response.setCommitted(false);
        response.reset();
        mockFilterChain.reset();
        ServletContext servletContext = request.getServletContext();
        HttpSession httpSession = request.getSession();
        request = new MockHttpServletRequest(servletContext);
        request.setSession(httpSession);
    }

    protected String jspPath(String viewName) {
        return "/WEB-INF/views/" + viewName + ".jsp";
    }

    protected CsrfToken loadCsrfToken() {
        CsrfToken csrfToken = CsrfToken.class.cast(
                request.getSession(false).getAttribute(HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN")));
        return csrfToken;
    }

    protected CsrfToken getCsrfToken() throws IOException, ServletException {
        get("/").perform();
        CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));
        return csrfToken;
    }


    protected Login login() {
        return new Login();
    }

    protected Authentication authentication() {
        return new Authentication();
    }

    protected Get get(String path) {
        return new Get().path(path);
    }

    protected class Get {
        private String path;

        public Get path(String path) {
            this.path = path;
            return this;
        }

        public void perform() throws IOException, ServletException {
            request.setServletPath(path);
            request.setRequestURI(path);
            request.setMethod(HttpMethod.GET.name());
            request.addHeader("Accept", "text/html");
        }
    }

    protected class Authentication {
        private String username;
        private String password;
        private CsrfToken csrfToken;

        public Authentication username(String username) {
            this.username = username;
            return this;
        }

        public Authentication password(String password) {
            this.password = password;
            return this;
        }

        public Authentication csrfToken(CsrfToken csrfToken) {
            this.csrfToken = csrfToken;
            return this;
        }

        public void perform() throws IOException, ServletException {
            reset();
            request.setRequestURI("/login");
            request.setServletPath("/login");
            request.setParameter("username", username);
            request.setParameter("password", password);
            request.setParameter(csrfToken.getParameterName(), csrfToken.getToken());
            request.setMethod(HttpMethod.POST.name());
            filterChainProxy.doFilter(request, response, mockFilterChain);
        }

    }

    protected class Login {
        private Authentication authentication = new Authentication();

        public Login username(String username) {
            authentication.username(username);
            return this;
        }

        public Login password(String password) {
            authentication.password(password);
            return this;
        }

        public void perform() throws IOException, ServletException {
            reset();

            request.setServletPath("/login");
            request.setRequestURI("/login");
            request.setMethod(HttpMethod.GET.name());
            request.addHeader("Accept", "text/html");

            filterChainProxy.doFilter(request, response, mockFilterChain);

            CsrfToken csrfToken = CsrfToken.class.cast(request.getAttribute(CsrfToken.class.getName()));

            authentication.csrfToken(csrfToken);
            authentication.perform();

            reset();
        }

    }

}
