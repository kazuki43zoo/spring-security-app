package springsecurity.app.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller("/login")
public class LoginController {

    @ModelAttribute
    public LoginForm setupLoginForm() {
        return new LoginForm("demo", "demo");
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorized() {
        return view();
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String view() {
        return "auth/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Validated LoginForm form, BindingResult result) {
        if (result.hasErrors()) {
            return view();
        }
        return "forward:/authenticate";
    }
}
