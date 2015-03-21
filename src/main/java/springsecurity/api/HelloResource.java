package springsecurity.api;

import java.io.Serializable;

public class HelloResource implements Serializable {

    private final String message;

    public HelloResource(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
