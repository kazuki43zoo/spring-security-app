package springsecurity.itest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import springsecurity.WebApplicationInitializer;
import springsecurity.WebSecurityConfig;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@WebAppConfiguration
@ContextConfiguration(classes = {
        ITestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class ITestSupport {

    WebApplicationContext wac; // cached

    @Autowired
    MockServletContext servletContext; // cached

    @Autowired
    MockHttpSession session;

    @Autowired
    MockHttpServletRequest request;

    @Autowired
    MockHttpServletResponse response;

    MockFilterChain mockFilterChain;

    @Before
    public void setupMockFilterChain() {
        this.mockFilterChain = new MockFilterChain();
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

}
