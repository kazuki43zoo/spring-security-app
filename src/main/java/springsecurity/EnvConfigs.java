package springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
class EnvConfigs {

    @Profile({"default", "local"})
    static class LocalEnvConfig {

        @Bean
        DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .setName("spring-security")
                    .setScriptEncoding(WebApplicationInitializer.DEFAULT_CHARACTER_ENCODING.name())
                    .addScript("classpath:/database/ddl.sql")
                    .addScript("classpath:/database/dml.sql")
                    .build();
        }

    }

}
