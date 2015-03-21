package springsecurity.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // not initialize in this class.
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{AppConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected String getServletName() {
        return "appServlet";
    }

    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackageClasses = AppConfig.class)
    static class AppConfig extends WebMvcConfigurerAdapter {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.beanName();
            registry.jsp().
                    prefix("/WEB-INF/views/");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources");
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/")
                    .setViewName("welcome/home");
            registry.addViewController("/login").
                    setViewName("auth/login");
            registry.addViewController("/unauthorized")
                    .setStatusCode(HttpStatus.UNAUTHORIZED)
                    .setViewName("auth/login");
        }
    }

}
