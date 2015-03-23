package springsecurity.api.hello;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springsecurity.core.setting.ApplicationSetting;
import springsecurity.domain.service.security.AccountUserDetails;

@RestController
@RequestMapping("/")
public class HelloRestController {

    @Autowired
    ApplicationSetting applicationSetting;

    @RequestMapping
    public HelloResource hello(@AuthenticationPrincipal AccountUserDetails userDetails) {
        String message = "Welcome " + userDetails.getUsername() + " on the " + applicationSetting.getName() + " !";
        return new HelloResource(message);
    }

}
