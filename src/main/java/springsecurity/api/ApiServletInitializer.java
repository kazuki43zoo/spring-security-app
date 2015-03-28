package springsecurity.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import springsecurity.support.config.SpringJavaConfigSupport.PropertyConfig;

public class ApiServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // not initialize in this class.
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{ApiServletConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/api/*"};
    }

    @Override
    protected String getServletName() {
        return "apiServlet";
    }

    @Configuration
    @EnableWebMvc
    @Import(PropertyConfig.class)
    @ComponentScan(basePackageClasses = ApiServletConfig.class)
    public static class ApiServletConfig extends WebMvcConfigurerAdapter {
    }

}
