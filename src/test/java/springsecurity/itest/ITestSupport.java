package springsecurity.itest;

import org.junit.Before;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebAppConfiguration
@ContextConfiguration(classes = {
        ITestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class ITestSupport {

    private static final String CSRF_TOKEN_SESSION_KEY = HttpSessionCsrfTokenRepository.class.getName() + ".CSRF_TOKEN";
    private static final String CSRF_TOKEN_REQUEST_KEY = CsrfToken.class.getName();
    private static final String BINDING_RESULT_BASE_KEY = BindingResult.class.getName() + ".";

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

    DispatcherServlet dispatcherServlet;

    MockFilterChain mockFilterChain;

    @Before
    public void setupMockFilterChain() throws ServletException {
        this.mockFilterChain = createMockFilterChain();
    }

    protected MockFilterChain createMockFilterChain() throws ServletException {
        this.dispatcherServlet = createDispatcherServlet();
        dispatcherServlet.init(new MockServletConfig("dispatcherServlet"));
        return new MockFilterChain(dispatcherServlet);
    }

    protected DispatcherServlet createDispatcherServlet() throws ServletException {
        return new DispatcherServlet(wac);
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

    protected void invalidSession() {
        request.setRequestedSessionId(UUID.randomUUID().toString());
        request.setRequestedSessionIdValid(false);
        request.setSession(null);
    }

    protected String jspPath(String viewName) {
        return "/WEB-INF/views/" + viewName + ".jsp";
    }


    protected CsrfToken loadCsrfToken() {
        CsrfToken csrfToken = CsrfToken.class.cast(
                request.getSession(false).getAttribute(CSRF_TOKEN_SESSION_KEY));
        if (csrfToken != null) {
            return csrfToken;
        }
        csrfToken = CsrfToken.class.cast(request.getAttribute(CSRF_TOKEN_REQUEST_KEY));
        if (csrfToken != null) {
            csrfToken.getToken();
        }
        return csrfToken;
    }

    protected CsrfToken getCsrfToken() throws IOException, ServletException {
        get("/").perform();
        return CsrfToken.class.cast(request.getAttribute(CSRF_TOKEN_REQUEST_KEY));
    }

    protected BindingResult getBindingResult(String modelName) {
        String attributeName = BINDING_RESULT_BASE_KEY + modelName;
        return BindingResult.class.cast(request.getAttribute(attributeName));
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

    protected Post post(String path) {
        return new Post().path(path);
    }

    private abstract class Request<T> {
        private String path = "/";
        protected HttpMethod method = HttpMethod.GET;
        private CsrfToken csrfToken;
        private boolean reset = true;
        private boolean security = true;

        @SuppressWarnings("unchecked")
        public T path(String path) {
            this.path = path;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T method(HttpMethod method) {
            this.method = method;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T csrfToken(CsrfToken csrfToken) {
            this.csrfToken = csrfToken;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T reset(boolean reset) {
            this.reset = reset;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T security(boolean security) {
            this.security = security;
            return (T) this;
        }

        public void perform() throws IOException, ServletException {
            if (reset) {
                ITestSupport.this.reset();
            }
            request.setServletPath(path);
            request.setRequestURI(path);
            request.setMethod(method.name());
            request.addHeader("Accept", "text/html");
            if (csrfToken != null) {
                request.setParameter(csrfToken.getParameterName(), csrfToken.getToken());
            }
            setupRequest();
            if (security) {
                filterChainProxy.doFilter(request, response, mockFilterChain);
            } else {
                dispatcherServlet.service(request, response);
            }
        }

        protected void setupRequest() {
        }
    }

    protected class Post extends Request<Post> {
        private Post() {
            method(HttpMethod.POST).csrfToken(loadCsrfToken());
        }
    }

    protected class Get extends Request<Get> {
        private Get() {
        }
    }

    protected class Authentication extends Request<Authentication> {
        private String username;
        private String password;

        private Authentication() {
            path("/authenticate").method(HttpMethod.POST).csrfToken(loadCsrfToken());
        }

        public Authentication username(String username) {
            this.username = username;
            return this;
        }

        public Authentication password(String password) {
            this.password = password;
            return this;
        }

        public void setupRequest() {
            request.setRequestURI("/login");
            request.setParameter("username", username);
            request.setParameter("password", password);
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

            get("/login").perform();

            authentication.csrfToken(loadCsrfToken()).perform();

            reset();
        }

    }

}
