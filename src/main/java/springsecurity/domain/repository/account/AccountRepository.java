package springsecurity.domain.repository.account;

import org.apache.ibatis.annotations.Select;
import springsecurity.domain.model.Account;

public interface AccountRepository {
    @Select("SELECT username, password, first_name, last_name " +
            "FROM account " +
            "WHERE username = #{username}")
    Account findOne(String username);
}
