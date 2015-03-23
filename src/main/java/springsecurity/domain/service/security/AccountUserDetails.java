package springsecurity.domain.service.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import springsecurity.domain.model.Account;

public class AccountUserDetails extends User {

    private final Account account;

    public AccountUserDetails(Account account) {
        super(account.getUsername(), account.getPassword(), AuthorityUtils
                .createAuthorityList("ROLE_USER"));
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

}
