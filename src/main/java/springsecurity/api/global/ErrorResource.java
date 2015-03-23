package springsecurity.api.global;

import java.io.Serializable;

public class ErrorResource implements Serializable {
    private final String code;
    private final String message;

    public ErrorResource(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
