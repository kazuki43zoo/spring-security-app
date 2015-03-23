package springsecurity.core.setting;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ApplicationSetting implements Serializable {

    static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
