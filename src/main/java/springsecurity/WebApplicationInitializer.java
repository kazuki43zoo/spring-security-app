package springsecurity;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

public class WebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    public static final String DEFAULT_CHARACTER_ENCODING = StandardCharsets.UTF_8.name();

    public WebApplicationInitializer() {
        super(AppConfig.class, EnvConfigs.class, WebSecurityConfig.class);
    }

    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        insertFilters(servletContext, characterEncodingFilter());
    }

    @Override
    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
    }

    private CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(DEFAULT_CHARACTER_ENCODING);
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Configuration
    @EnableTransactionManagement
    @ComponentScan("springsecurity.domain")
    @MapperScan("springsecurity.domain.repository")
    static class AppConfig {

        @Bean
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            SqlSessionFactory factory =
                    SqlSessionFactory.class.cast(sqlSessionFactoryBean.getObject());
            factory.getConfiguration().setMapUnderscoreToCamelCase(true);
            return factory;
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

    }

}
