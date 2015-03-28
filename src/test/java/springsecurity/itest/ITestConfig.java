package springsecurity.itest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ValidatorFactory;
import javax.xml.validation.Validator;

@Configuration
@ComponentScan(basePackageClasses = ITestConfig.class)
public class ITestConfig {
}
