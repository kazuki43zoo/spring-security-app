package springsecurity.api.hello;


import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springsecurity.domain.service.security.AccountUserDetails;

@RestController
@RequestMapping("/")
public class HelloRestController {

    @RequestMapping
    public HelloResource hello(@AuthenticationPrincipal AccountUserDetails userDetails) {
        return new HelloResource("hello " + userDetails.getUsername() + "!");
    }

}
