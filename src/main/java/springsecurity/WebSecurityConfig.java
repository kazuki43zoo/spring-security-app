package springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static springsecurity.support.config.SpringSecurityJavaConfigSupport.*;

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
        // authorize
        http.authorizeRequests()
                .antMatchers("/login", "/authenticate").anonymous()
                .anyRequest().authenticated();
        http.exceptionHandling().
                defaultAuthenticationEntryPointFor(
                        sendErrorEntryPoint(HttpStatus.UNAUTHORIZED), antRequestMatcher("/api/**"));
        // authenticate
        http.formLogin()
                .loginPage("/login?error=unauthorized")
                .loginProcessingUrl("/authenticate")
                .failureHandler(failureUrl("/login?error=failed", true));
        http.logout()
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID");
        // session management
        http.sessionManagement()
                .invalidSessionUrl("/login?invalidSession")
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
