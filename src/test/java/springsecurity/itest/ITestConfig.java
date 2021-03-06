package springsecurity.itest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ITestConfig.class)
public class ITestConfig {
}
