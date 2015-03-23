package springsecurity.app.account;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springsecurity.domain.model.Account;
import springsecurity.domain.service.security.AccountUserDetails;

@Controller
@RequestMapping("/account")
public class AccountController {

    @RequestMapping
    public String view(
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model) {
        Account account = userDetails.getAccount();
        model.addAttribute(account);
        return "account/detail";
    }

}
