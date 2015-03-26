package springsecurity.core.setting;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SecuritySetting implements Serializable {

    static final long serialVersionUID = 1L;

    private String demoUsername;
    private String demoPassword;

    public String getDemoUsername() {
        return demoUsername;
    }

    public void setDemoUsername(String demoUsername) {
        this.demoUsername = demoUsername;
    }

    public String getDemoPassword() {
        return demoPassword;
    }

    public void setDemoPassword(String demoPassword) {
        this.demoPassword = demoPassword;
    }

}
