package springsecurity.domain.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.domain.model.Account;
import springsecurity.domain.repository.account.AccountRepository;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findOne(username);
        if (account == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return new AccountUserDetails(account);
    }

}
