package springsecurity.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import springsecurity.support.config.SpringJavaConfigSupport.PropertyConfig;

public class AppServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // not initialize in this class.
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{AppServletConfig.class};
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
    @Import(PropertyConfig.class)
    @ComponentScan(basePackageClasses = AppServletConfig.class)
    static class AppServletConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.beanName();
            registry.jsp().
                    prefix("/WEB-INF/views/");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources")
                    .resourceChain(false)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/")
                    .setViewName("welcome/home");
        }

    }

}
