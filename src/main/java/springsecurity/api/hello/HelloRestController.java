package springsecurity.api.hello;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloRestController {

    @RequestMapping
    public HelloResource hello() {
        return new HelloResource("hello !");
    }

}
