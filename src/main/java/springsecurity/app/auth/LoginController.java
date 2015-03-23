package springsecurity.app.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/login")
public class LoginController {

    @ModelAttribute
    public LoginForm setupLoginForm() {
        return new LoginForm("demo", "demo");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view() {
        return "auth/login";
    }

    @RequestMapping(params = "error=unauthorized", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorized() {
        return view();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String login(@Validated LoginForm form, BindingResult result) {
        if (result.hasErrors()) {
            return view();
        }
        return "forward:/authenticate";
    }

    @RequestMapping(params = "error=failed", method = RequestMethod.POST)
    public String loginFailed(LoginForm form) {
        return view();
    }

}
