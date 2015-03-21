package springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/unauthorized", "/login").anonymous()
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/unauthorized")
                .loginProcessingUrl("/login")
                .failureUrl("/login?error");
        http.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID");
        http.sessionManagement()
                .invalidSessionUrl("/")
                .sessionFixation().migrateSession()
                .maximumSessions(1).expiredUrl("/");
    }

    @Autowired
    public void configureDaoAuthentication(
            AuthenticationManagerBuilder auth,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
